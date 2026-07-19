package com.supermarkettracker.infrastructure.integration.mock;

import com.supermarkettracker.domain.gateway.CartaoGateway;
import com.supermarkettracker.domain.model.enums.ModalidadeCartao;
import com.supermarkettracker.domain.model.valueobject.Dinheiro;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import org.springframework.stereotype.Component;

@Component
public class CartaoMockAdapter implements CartaoGateway {
    @Override
    public TransacaoCartao processar(Identificador pagamentoId, Dinheiro valor,
                                     ModalidadeCartao modalidade, short parcelas) {
        String referencia = "mock-cartao-" + pagamentoId.valor();
        String autorizacao = "MOCK-" + pagamentoId.valor().toString().substring(0, 8).toUpperCase();
        return new TransacaoCartao(referencia, "APROVADO", autorizacao);
    }
}
