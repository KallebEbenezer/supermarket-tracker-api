package com.supermarkettracker.infrastructure.integration.mock;

import com.supermarkettracker.domain.gateway.CartaoGateway;
import com.supermarkettracker.domain.gateway.PixGateway;
import com.supermarkettracker.domain.gateway.ProcessadorPagamentoGateway;
import com.supermarkettracker.domain.model.enums.ModalidadeCartao;
import com.supermarkettracker.domain.model.valueobject.Dinheiro;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import org.springframework.stereotype.Component;

/** Compatibiliza o contrato de pagamentos já usado pela aplicação com as portas específicas. */
@Component
public class ProcessadorPagamentoMockAdapter implements ProcessadorPagamentoGateway {
    private final PixGateway pixGateway;
    private final CartaoGateway cartaoGateway;

    public ProcessadorPagamentoMockAdapter(PixGateway pixGateway, CartaoGateway cartaoGateway) {
        this.pixGateway = pixGateway;
        this.cartaoGateway = cartaoGateway;
    }

    @Override
    public ResultadoPagamento processarPix(Identificador pagamentoId, Dinheiro valor) {
        PixGateway.CobrancaPix cobranca = pixGateway.criarCobranca(pagamentoId, valor);
        return new ResultadoPagamento(cobranca.referenciaExterna(), cobranca.status(), null);
    }

    @Override
    public ResultadoPagamento processarCartao(Identificador pagamentoId, Dinheiro valor,
                                              ModalidadeCartao modalidade, short parcelas) {
        CartaoGateway.TransacaoCartao transacao = cartaoGateway.processar(pagamentoId, valor, modalidade, parcelas);
        return new ResultadoPagamento(transacao.referenciaExterna(), transacao.status(), transacao.codigoAutorizacao());
    }
}
