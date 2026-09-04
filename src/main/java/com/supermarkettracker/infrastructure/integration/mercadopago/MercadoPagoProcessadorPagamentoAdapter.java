package com.supermarkettracker.infrastructure.integration.mercadopago;

import com.supermarkettracker.domain.gateway.CartaoGateway;
import com.supermarkettracker.domain.gateway.PixGateway;
import com.supermarkettracker.domain.gateway.ProcessadorPagamentoGateway;
import com.supermarkettracker.domain.model.enums.ModalidadeCartao;
import com.supermarkettracker.domain.model.valueobject.Dinheiro;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.cartao.provider", havingValue = "mercadopago", matchIfMissing = true)
public class MercadoPagoProcessadorPagamentoAdapter implements ProcessadorPagamentoGateway {
    private final PixGateway pixGateway;
    private final CartaoGateway cartaoGateway;

    public MercadoPagoProcessadorPagamentoAdapter(PixGateway pixGateway, CartaoGateway cartaoGateway) {
        this.pixGateway = pixGateway;
        this.cartaoGateway = cartaoGateway;
    }

    @Override
    public ResultadoPagamento processarPix(Identificador pagamentoId, Dinheiro valor) {
        PixGateway.CobrancaPix cobranca = pixGateway.criarCobranca(pagamentoId, valor, "mock-chave-pix");
        return new ResultadoPagamento(cobranca.transacaoId(), cobranca.status(), null);
    }

    @Override
    public ResultadoPagamento processarCartao(Identificador pagamentoId, Dinheiro valor,
                                              ModalidadeCartao modalidade, short parcelas) {
        CartaoGateway.TransacaoCartao transacao = cartaoGateway.processar(pagamentoId, valor, modalidade, parcelas, null);
        return new ResultadoPagamento(transacao.transacaoId(), transacao.status(), transacao.codigoAutorizacao());
    }
}
