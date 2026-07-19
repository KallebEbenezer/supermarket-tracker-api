package com.supermarkettracker.infrastructure.web.dto;

import com.supermarkettracker.domain.model.SessaoCaixa;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record SessaoCaixaResponse(UUID id, UUID caixaId, String status, Instant abertoEm, Instant fechadoEm,
                                  BigDecimal valorAbertura, BigDecimal valorFechamentoInformado, String observacao) {
    public static SessaoCaixaResponse from(SessaoCaixa sessao) {
        return new SessaoCaixaResponse(sessao.id().valor(), sessao.caixaId().valor(), sessao.status().name(),
                sessao.abertoEm(), sessao.fechadoEm(), sessao.valorAbertura().valor(),
                sessao.valorFechamentoInformado() == null ? null : sessao.valorFechamentoInformado().valor(), sessao.observacao());
    }
}
