package com.supermarkettracker.infrastructure.web.dto;

import com.supermarkettracker.domain.model.Pagamento;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PagamentoResponse(
    UUID id,
    UUID vendaId,
    String tipo,
    BigDecimal valor,
    String status,
    String referencia,
    Instant recebidoEm
) {
    public static PagamentoResponse of(Pagamento p) {
        return new PagamentoResponse(
            p.id().valor(),
            p.vendaId().valor(),
            p.tipo().name(),
            p.valor().valor(),
            p.status().name(),
            p.referencia(),
            p.recebidoEm()
        );
    }
}
