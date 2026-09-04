package com.supermarkettracker.domain.gateway;

import com.supermarkettracker.domain.model.enums.ModalidadeCartao;
import com.supermarkettracker.domain.model.valueobject.Dinheiro;
import com.supermarkettracker.domain.model.valueobject.Identificador;

/** Porta para adquirentes ou gateways de pagamento com cartão. */
public interface CartaoGateway {
    TransacaoCartao processar(Identificador pagamentoId, Dinheiro valor,
                              ModalidadeCartao modalidade, short parcelas, String tokenCartao);

    record TransacaoCartao(String gateway, String transacaoId, String status, String codigoAutorizacao) { }
}
