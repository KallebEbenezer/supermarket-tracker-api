package com.supermarkettracker.domain.gateway;

import com.supermarkettracker.domain.model.valueobject.Dinheiro;
import com.supermarkettracker.domain.model.valueobject.Identificador;

/** Porta para provedores de cobrança PIX. */
public interface PixGateway {
    CobrancaPix criarCobranca(Identificador pagamentoId, Dinheiro valor, String pixKey);

    record CobrancaPix(
        String gateway,
        String transacaoId,
        String status,
        String qrCode,
        String copiaECola,
        String expiracao
    ) { }
}
