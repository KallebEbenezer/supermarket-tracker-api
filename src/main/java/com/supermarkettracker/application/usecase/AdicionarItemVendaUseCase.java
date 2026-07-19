package com.supermarkettracker.application.usecase;

import com.supermarkettracker.application.command.AdicionarItemVendaCommand;
import com.supermarkettracker.application.validator.ValidacaoCommand;
import com.supermarkettracker.domain.model.ItemVenda;
import com.supermarkettracker.domain.model.MovimentacaoEstoque;
import com.supermarkettracker.domain.model.Produto;
import com.supermarkettracker.domain.model.Venda;
import com.supermarkettracker.domain.model.enums.TipoMovimentacaoEstoque;
import com.supermarkettracker.domain.model.valueobject.Dinheiro;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.model.valueobject.Quantidade;
import com.supermarkettracker.domain.repository.VendaRepository;
import com.supermarkettracker.domain.repository.ProdutoRepository;
import com.supermarkettracker.domain.exception.EntidadeNaoEncontradaException;
import com.supermarkettracker.domain.exception.RegraDeDominioException;
import java.math.BigDecimal;
import java.time.Instant;

public final class AdicionarItemVendaUseCase {
    private final VendaRepository vendas;
    private final ProdutoRepository produtos;

    public AdicionarItemVendaUseCase(VendaRepository vendas, ProdutoRepository produtos) {
        this.vendas = vendas;
        this.produtos = produtos;
    }

    public ItemVenda executar(AdicionarItemVendaCommand c) {
        ValidacaoCommand.obrigatorio(c.vendaId(), "Venda");
        ValidacaoCommand.obrigatorio(c.produtoNome(), "Produto");
        ValidacaoCommand.positivo(c.quantidade(), "Quantidade");
        ValidacaoCommand.naoNegativo(c.precoUnitario(), "Preco unitario");
        ValidacaoCommand.naoNegativo(c.precoCompraUnitario(), "Preco de compra");
        ValidacaoCommand.naoNegativo(c.desconto(), "Desconto");
        ValidacaoCommand.naoNegativo(c.acrescimo(), "Acrescimo");
        BigDecimal subtotal = c.precoUnitario().multiply(c.quantidade()).subtract(c.desconto()).add(c.acrescimo());
        BigDecimal custo = c.precoCompraUnitario().multiply(c.quantidade());
        ItemVenda item = new ItemVenda(Identificador.novo(), new Identificador(c.vendaId()),
                c.produtoId() == null ? null : new Identificador(c.produtoId()), c.numero(), c.produtoNome(),
                c.codigoBarras(), c.unidadeMedida(), new Quantidade(c.quantidade()), new Dinheiro(c.precoUnitario()),
                new Dinheiro(c.precoCompraUnitario()), new Dinheiro(c.desconto()), new Dinheiro(c.acrescimo()),
                new Dinheiro(subtotal), new Dinheiro(custo), new Dinheiro(subtotal.subtract(custo)), Instant.now());
        if (item.produtoId() == null) return vendas.salvarItem(item);

        Venda venda = vendas.buscarPorId(item.vendaId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Venda nao encontrada"));
        Produto produto = produtos.buscarPorId(item.produtoId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Produto nao encontrado"));
        BigDecimal anterior = produto.estoqueAtual().valor();
        BigDecimal posterior = anterior.subtract(item.quantidade().valor());
        if (posterior.signum() < 0 && !produto.permiteEstoqueNegativo()) {
            throw new RegraDeDominioException("Estoque insuficiente");
        }
        MovimentacaoEstoque baixa = new MovimentacaoEstoque(Identificador.novo(), venda.empresaId(), venda.lojaId(),
                produto.id(), venda.usuarioId(), venda.id(), item.id(), TipoMovimentacaoEstoque.VENDA,
                item.quantidade(), new Quantidade(anterior), new Quantidade(posterior), item.precoCompraUnitario(),
                "Baixa automática da venda " + venda.numero(), "VENDA-" + venda.numero(), Instant.now());
        return vendas.salvarItemComBaixaEstoque(item, baixa);
    }
}
