package com.supermarkettracker.infrastructure.web.dto;

import com.supermarkettracker.domain.model.Caixa;
import java.util.UUID;

public record CaixaResponse(UUID id, UUID lojaId, String codigo, String nome, String status) {
    public static CaixaResponse from(Caixa caixa) {
        return new CaixaResponse(caixa.id().valor(), caixa.lojaId().valor(), caixa.codigo(), caixa.nome(), caixa.status().name());
    }
}
