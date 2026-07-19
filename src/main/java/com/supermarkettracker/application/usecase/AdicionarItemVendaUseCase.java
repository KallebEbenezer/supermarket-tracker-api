package com.supermarkettracker.application.usecase;
import com.supermarkettracker.application.command.AdicionarItemVendaCommand;
import com.supermarkettracker.application.validator.ValidacaoCommand;
import com.supermarkettracker.domain.model.ItemVenda;
import com.supermarkettracker.domain.model.valueobject.Dinheiro;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.model.valueobject.Quantidade;
import com.supermarkettracker.domain.repository.VendaRepository;
import java.math.BigDecimal;
import java.time.Instant;
public final class AdicionarItemVendaUseCase { private final VendaRepository vendas; public AdicionarItemVendaUseCase(VendaRepository vendas) { this.vendas = vendas; }
    public ItemVenda executar(AdicionarItemVendaCommand c) { ValidacaoCommand.obrigatorio(c.vendaId(), "Venda"); ValidacaoCommand.obrigatorio(c.produtoNome(), "Produto"); ValidacaoCommand.positivo(c.quantidade(), "Quantidade"); ValidacaoCommand.naoNegativo(c.precoUnitario(), "Preco unitario"); ValidacaoCommand.naoNegativo(c.precoCompraUnitario(), "Preco de compra"); ValidacaoCommand.naoNegativo(c.desconto(), "Desconto"); ValidacaoCommand.naoNegativo(c.acrescimo(), "Acrescimo"); BigDecimal subtotal = c.precoUnitario().multiply(c.quantidade()).subtract(c.desconto()).add(c.acrescimo()); BigDecimal custo = c.precoCompraUnitario().multiply(c.quantidade()); return vendas.salvarItem(new ItemVenda(Identificador.novo(), new Identificador(c.vendaId()), c.produtoId() == null ? null : new Identificador(c.produtoId()), c.numero(), c.produtoNome(), c.codigoBarras(), c.unidadeMedida(), new Quantidade(c.quantidade()), new Dinheiro(c.precoUnitario()), new Dinheiro(c.precoCompraUnitario()), new Dinheiro(c.desconto()), new Dinheiro(c.acrescimo()), new Dinheiro(subtotal), new Dinheiro(custo), new Dinheiro(subtotal.subtract(custo)), Instant.now())); } }
