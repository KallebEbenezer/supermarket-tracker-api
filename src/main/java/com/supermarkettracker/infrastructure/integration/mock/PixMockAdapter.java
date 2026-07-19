package com.supermarkettracker.infrastructure.integration.mock;

import com.supermarkettracker.domain.gateway.PixGateway;
import com.supermarkettracker.domain.model.valueobject.Dinheiro;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import org.springframework.stereotype.Component;

@Component
public class PixMockAdapter implements PixGateway {
    @Override
    public CobrancaPix criarCobranca(Identificador pagamentoId, Dinheiro valor) {
        String referencia = "mock-pix-" + pagamentoId.valor();
        String copiaECola = "PIX-MOCK:" + referencia + ":" + valor.valor().toPlainString();
        return new CobrancaPix("mock", referencia, "APROVADO", null, copiaECola);
    }
}
