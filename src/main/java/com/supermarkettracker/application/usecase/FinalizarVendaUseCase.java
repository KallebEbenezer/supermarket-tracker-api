package com.supermarkettracker.application.usecase;

import com.supermarkettracker.application.command.FinalizarVendaCommand;
import com.supermarkettracker.application.command.ItemVendaCheckoutCommand;
import com.supermarkettracker.application.command.PagamentoCheckoutCommand;
import com.supermarkettracker.application.dto.VendaDto;
import com.supermarkettracker.application.mapper.VendaMapper;
import com.supermarkettracker.application.validator.ValidacaoCommand;
import com.supermarkettracker.domain.exception.EntidadeNaoEncontradaException;
import com.supermarkettracker.domain.exception.RegraDeDominioException;
import com.supermarkettracker.domain.gateway.ProcessadorPagamentoGateway;
import com.supermarkettracker.domain.model.ItemVenda;
import com.supermarkettracker.domain.model.MovimentacaoEstoque;
import com.supermarkettracker.domain.model.Pagamento;
import com.supermarkettracker.domain.model.Produto;
import com.supermarkettracker.domain.model.Venda;
import com.supermarkettracker.domain.model.enums.StatusPagamento;
import com.supermarkettracker.domain.model.enums.StatusVenda;
import com.supermarkettracker.domain.model.enums.TipoMovimentacaoEstoque;
import com.supermarkettracker.domain.model.enums.TipoPagamento;
import com.supermarkettracker.domain.model.valueobject.Dinheiro;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.model.valueobject.Quantidade;
import com.supermarkettracker.domain.repository.DashboardRepository;
import com.supermarkettracker.domain.repository.MovimentacaoEstoqueRepository;
import com.supermarkettracker.domain.repository.PagamentoRepository;
import com.supermarkettracker.domain.repository.ProdutoRepository;
import com.supermarkettracker.domain.repository.VendaRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public class FinalizarVendaUseCase {
    private final VendaRepository vendas;
    private final ProdutoRepository produtos;
    private final PagamentoRepository pagamentos;
    private final MovimentacaoEstoqueRepository movimentacoes;
    private final DashboardRepository dashboard;
    private final ProcessadorPagamentoGateway processador;

    public FinalizarVendaUseCase(VendaRepository vendas, ProdutoRepository produtos, PagamentoRepository pagamentos,
            MovimentacaoEstoqueRepository movimentacoes, DashboardRepository dashboard,
            ProcessadorPagamentoGateway processador) {
        this.vendas = vendas;
        this.produtos = produtos;
        this.pagamentos = pagamentos;
        this.movimentacoes = movimentacoes;
        this.dashboard = dashboard;
        this.processador = processador;
    }

    @Transactional
    public VendaDto executar(FinalizarVendaCommand command) {
        validar(command);
        List<ItemVenda> itens = command.itens().stream()
                .map(item -> montarItem(command, item))
                .toList();
        BigDecimal subtotal = itens.stream().map(item -> item.subtotal().valor())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal quantidadeItens = itens.stream().map(item -> item.quantidade().valor())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal custoTotal = itens.stream().map(item -> item.custoTotal().valor())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal total = subtotal.subtract(command.desconto()).add(command.acrescimo());
        if (total.signum() < 0) {
            throw new RegraDeDominioException("Total da venda nao pode ser negativo");
        }
        validarTotalPagamentos(command.pagamentos(), total);

        Instant agora = Instant.now();
        BigDecimal resultado = total.subtract(custoTotal);
        Venda venda = new Venda(Identificador.novo(), new Identificador(command.empresaId()),
                new Identificador(command.lojaId()), identificador(command.sessaoCaixaId()),
                new Identificador(command.usuarioId()), identificador(command.clienteId()), command.numero(),
                new Dinheiro(subtotal), new Dinheiro(command.desconto()), new Dinheiro(command.acrescimo()),
                new Dinheiro(total), new Dinheiro(custoTotal), new Dinheiro(resultado.max(BigDecimal.ZERO)),
                new Dinheiro(resultado.min(BigDecimal.ZERO).abs()), new Quantidade(quantidadeItens),
                StatusVenda.AGUARDANDO_PAGAMENTO, null, null, null, command.observacao(), agora, agora);
        Venda vendaCriada = vendas.salvar(venda);

        List<ItemVenda> itensSalvos = itens.stream().map(item -> vincularAVenda(item, vendaCriada.id()))
                .map(vendas::salvarItem).toList();
        registrarPagamentos(vendaCriada, command.pagamentos());
        venda = vendas.salvar(finalizar(vendaCriada));
        movimentarEstoque(venda, itensSalvos);
        dashboard.registrarVendaPaga(venda);
        return VendaMapper.paraDto(venda);
    }

    private void validar(FinalizarVendaCommand command) {
        ValidacaoCommand.obrigatorio(command.empresaId(), "Empresa");
        ValidacaoCommand.obrigatorio(command.lojaId(), "Loja");
        ValidacaoCommand.obrigatorio(command.usuarioId(), "Usuario");
        if (command.numero() <= 0) throw new IllegalArgumentException("Numero da venda deve ser positivo");
        ValidacaoCommand.naoNegativo(command.desconto(), "Desconto");
        ValidacaoCommand.naoNegativo(command.acrescimo(), "Acrescimo");
        if (command.itens() == null || command.itens().isEmpty()) {
            throw new RegraDeDominioException("A venda deve possuir ao menos um item");
        }
        if (command.itens().stream().anyMatch(java.util.Objects::isNull)) {
            throw new RegraDeDominioException("Item da venda e obrigatorio");
        }
        if (command.pagamentos() == null || command.pagamentos().isEmpty()) {
            throw new RegraDeDominioException("A venda deve possuir ao menos um pagamento");
        }
        for (PagamentoCheckoutCommand pagamento : command.pagamentos()) {
            if (pagamento == null) throw new RegraDeDominioException("Pagamento e obrigatorio");
            ValidacaoCommand.obrigatorio(pagamento.tipo(), "Tipo de pagamento");
            ValidacaoCommand.positivo(pagamento.valor(), "Valor do pagamento");
        }
    }

    private ItemVenda montarItem(FinalizarVendaCommand venda, ItemVendaCheckoutCommand command) {
        ValidacaoCommand.obrigatorio(command.produtoNome(), "Produto");
        ValidacaoCommand.obrigatorio(command.unidadeMedida(), "Unidade de medida");
        if (command.numero() <= 0) throw new IllegalArgumentException("Numero do item deve ser positivo");
        ValidacaoCommand.positivo(command.quantidade(), "Quantidade");
        ValidacaoCommand.naoNegativo(command.precoUnitario(), "Preco unitario");
        ValidacaoCommand.naoNegativo(command.precoCompraUnitario(), "Preco de compra");
        ValidacaoCommand.naoNegativo(command.desconto(), "Desconto do item");
        ValidacaoCommand.naoNegativo(command.acrescimo(), "Acrescimo do item");
        BigDecimal subtotal = command.precoUnitario().multiply(command.quantidade())
                .subtract(command.desconto()).add(command.acrescimo());
        if (subtotal.signum() < 0) throw new RegraDeDominioException("Subtotal do item nao pode ser negativo");
        BigDecimal custo = command.precoCompraUnitario().multiply(command.quantidade());
        return new ItemVenda(Identificador.novo(), null, identificador(command.produtoId()), command.numero(),
                command.produtoNome(), command.codigoBarras(), command.unidadeMedida(),
                new Quantidade(command.quantidade()), new Dinheiro(command.precoUnitario()),
                new Dinheiro(command.precoCompraUnitario()), new Dinheiro(command.desconto()),
                new Dinheiro(command.acrescimo()), new Dinheiro(subtotal), new Dinheiro(custo),
                new Dinheiro(subtotal.subtract(custo)), Instant.now());
    }

    private void registrarPagamentos(Venda venda, List<PagamentoCheckoutCommand> comandos) {
        for (PagamentoCheckoutCommand command : comandos) {
            ValidacaoCommand.obrigatorio(command.tipo(), "Tipo de pagamento");
            ValidacaoCommand.positivo(command.valor(), "Valor do pagamento");
            Identificador pagamentoId = Identificador.novo();
            String referencia = command.referencia();
            if (command.tipo() == TipoPagamento.PIX) {
                referencia = processador.processarPix(pagamentoId, new Dinheiro(command.valor())).referenciaExterna();
            } else if (command.tipo() == TipoPagamento.CARTAO) {
                ValidacaoCommand.obrigatorio(command.modalidadeCartao(), "Modalidade do cartao");
                if (command.parcelas() < 1) throw new IllegalArgumentException("Parcelas deve ser ao menos 1");
                referencia = processador.processarCartao(pagamentoId, new Dinheiro(command.valor()),
                        command.modalidadeCartao(), command.parcelas()).referenciaExterna();
            }
            pagamentos.salvar(new Pagamento(pagamentoId, venda.id(), identificador(command.contaBancariaId()),
                    command.tipo(), new Dinheiro(command.valor()), StatusPagamento.APROVADO, Instant.now(),
                    referencia, Instant.now(), Instant.now()));
        }
    }

    private void movimentarEstoque(Venda venda, List<ItemVenda> itens) {
        for (ItemVenda item : itens) {
            if (item.produtoId() == null) continue;
            Produto produto = produtos.buscarPorId(item.produtoId())
                    .orElseThrow(() -> new EntidadeNaoEncontradaException("Produto nao encontrado"));
            if (!produto.empresaId().equals(venda.empresaId())) {
                throw new RegraDeDominioException("Produto nao pertence a empresa da venda");
            }
            BigDecimal posterior = produto.estoqueAtual().valor().subtract(item.quantidade().valor());
            movimentacoes.salvar(new MovimentacaoEstoque(Identificador.novo(), venda.empresaId(), venda.lojaId(),
                    produto.id(), venda.usuarioId(), venda.id(), item.id(), TipoMovimentacaoEstoque.VENDA,
                    item.quantidade(), produto.estoqueAtual(), new Quantidade(posterior),
                    item.precoCompraUnitario(), "Baixa automatica da venda " + venda.numero(),
                    "VENDA-" + venda.numero() + "-ITEM-" + item.numero(), Instant.now()));
        }
    }

    private void validarTotalPagamentos(List<PagamentoCheckoutCommand> pagamentos, BigDecimal total) {
        BigDecimal totalPago = pagamentos.stream().map(PagamentoCheckoutCommand::valor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (totalPago.compareTo(total) != 0) {
            throw new RegraDeDominioException("O total dos pagamentos deve ser igual ao total da venda");
        }
    }

    private Venda finalizar(Venda venda) {
        Instant agora = Instant.now();
        return new Venda(venda.id(), venda.empresaId(), venda.lojaId(), venda.sessaoCaixaId(), venda.usuarioId(),
                venda.clienteId(), venda.numero(), venda.subtotal(), venda.desconto(), venda.acrescimo(), venda.total(),
                venda.custoTotal(), venda.lucro(), venda.prejuizo(), venda.quantidadeItens(), StatusVenda.PAGA,
                agora, null, null, venda.observacao(), venda.criadoEm(), agora);
    }

    private ItemVenda vincularAVenda(ItemVenda item, Identificador vendaId) {
        return new ItemVenda(item.id(), vendaId, item.produtoId(), item.numero(), item.produtoNome(),
                item.codigoBarras(), item.unidadeMedida(), item.quantidade(), item.precoUnitario(),
                item.precoCompraUnitario(), item.desconto(), item.acrescimo(), item.subtotal(), item.custoTotal(),
                item.lucro(), item.criadoEm());
    }

    private Identificador identificador(java.util.UUID id) {
        return id == null ? null : new Identificador(id);
    }
}
