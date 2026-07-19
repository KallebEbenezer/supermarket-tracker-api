package com.supermarkettracker.application.usecase;

import com.supermarkettracker.application.command.PagamentoCheckoutCommand;
import com.supermarkettracker.application.validator.ValidacaoCommand;
import com.supermarkettracker.domain.exception.RegraDeDominioException;
import com.supermarkettracker.domain.gateway.CartaoGateway;
import com.supermarkettracker.domain.gateway.PixGateway;
import com.supermarkettracker.domain.model.Pagamento;
import com.supermarkettracker.domain.model.PagamentoCartao;
import com.supermarkettracker.domain.model.PagamentoPix;
import com.supermarkettracker.domain.model.Venda;
import com.supermarkettracker.domain.model.enums.ModalidadeCartao;
import com.supermarkettracker.domain.model.enums.StatusPagamento;
import com.supermarkettracker.domain.model.enums.TipoPagamento;
import com.supermarkettracker.domain.model.valueobject.Dinheiro;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.PagamentoRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Processa a forma de pagamento sem acoplar a orquestração da venda aos gateways. */
final class CheckoutPagamentoService {
    private final PagamentoRepository pagamentos;
    private final PixGateway pixGateway;
    private final CartaoGateway cartaoGateway;

    CheckoutPagamentoService(PagamentoRepository pagamentos, PixGateway pixGateway, CartaoGateway cartaoGateway) {
        this.pagamentos = pagamentos;
        this.pixGateway = pixGateway;
        this.cartaoGateway = cartaoGateway;
    }

    void processar(Venda venda, List<PagamentoCheckoutCommand> comandos) {
        for (PagamentoCheckoutCommand comando : comandos) {
            validar(comando);
            salvarPagamento(venda, comando);
        }
    }

    private void salvarPagamento(Venda venda, PagamentoCheckoutCommand comando) {
        Identificador pagamentoId = Identificador.novo();
        Instant agora = Instant.now();
        Pagamento pagamento = new Pagamento(pagamentoId, venda.id(), identificador(comando.contaBancariaId()),
                comando.tipo(), new Dinheiro(comando.valor()), StatusPagamento.APROVADO, agora,
                comando.referencia(), agora, agora);
        if (comando.tipo() == TipoPagamento.PIX) {
            salvarPix(pagamento, pagamentoId, agora);
        } else if (comando.tipo() == TipoPagamento.CARTAO) {
            salvarCartao(pagamento, comando, pagamentoId);
        } else {
            pagamentos.salvar(pagamento);
        }
    }

    private void salvarPix(Pagamento pagamento, Identificador pagamentoId, Instant agora) {
        PixGateway.CobrancaPix cobranca = pixGateway.criarCobranca(pagamentoId, pagamento.valor());
        exigirAprovado(cobranca.status());
        pagamentos.salvarPix(comReferencia(pagamento, cobranca.transacaoId()), new PagamentoPix(pagamentoId,
                cobranca.qrCode(), cobranca.copiaECola(), cobranca.transacaoId(), cobranca.gateway(),
                cobranca.transacaoId(), cobranca.status(), agora));
    }

    private void salvarCartao(Pagamento pagamento, PagamentoCheckoutCommand comando, Identificador pagamentoId) {
        CartaoGateway.TransacaoCartao transacao = cartaoGateway.processar(pagamentoId, pagamento.valor(),
                comando.modalidadeCartao(), comando.parcelas());
        exigirAprovado(transacao.status());
        pagamentos.salvarCartao(comReferencia(pagamento, transacao.transacaoId()), new PagamentoCartao(pagamentoId,
                comando.modalidadeCartao(), comando.parcelas(), null, null, transacao.codigoAutorizacao(), null,
                transacao.gateway(), transacao.transacaoId(), transacao.status()));
    }

    private void validar(PagamentoCheckoutCommand pagamento) {
        ValidacaoCommand.obrigatorio(pagamento.tipo(), "Tipo de pagamento");
        ValidacaoCommand.positivo(pagamento.valor(), "Valor do pagamento");
        if (pagamento.tipo() != TipoPagamento.DINHEIRO && pagamento.tipo() != TipoPagamento.PIX
                && pagamento.tipo() != TipoPagamento.CARTAO) {
            throw new RegraDeDominioException("Tipo de pagamento nao suportado");
        }
        if (pagamento.tipo() == TipoPagamento.CARTAO) {
            ValidacaoCommand.obrigatorio(pagamento.modalidadeCartao(), "Modalidade do cartao");
            if (pagamento.parcelas() < 1) throw new RegraDeDominioException("Parcelas deve ser ao menos 1");
            if (pagamento.modalidadeCartao() == ModalidadeCartao.DEBITO && pagamento.parcelas() != 1) {
                throw new RegraDeDominioException("Pagamento no debito deve possuir uma parcela");
            }
        }
    }

    private void exigirAprovado(String status) {
        if (!"APROVADO".equalsIgnoreCase(status)) {
            throw new RegraDeDominioException("Pagamento nao foi aprovado pelo gateway");
        }
    }

    private Pagamento comReferencia(Pagamento pagamento, String referencia) {
        return new Pagamento(pagamento.id(), pagamento.vendaId(), pagamento.contaBancariaId(), pagamento.tipo(),
                pagamento.valor(), pagamento.status(), pagamento.recebidoEm(), referencia, pagamento.criadoEm(),
                pagamento.atualizadoEm());
    }

    private Identificador identificador(UUID id) {
        return id == null ? null : new Identificador(id);
    }
}
