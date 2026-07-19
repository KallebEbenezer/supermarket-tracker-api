package com.supermarkettracker.infrastructure.web.dto;

import com.supermarkettracker.domain.model.MovimentacaoEstoque;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record MovimentacaoEstoqueResponse(UUID id, UUID produtoId, String tipo, BigDecimal quantidade,
                                          BigDecimal estoqueAnterior, BigDecimal estoquePosterior, Instant criadoEm) {
    public static MovimentacaoEstoqueResponse from(MovimentacaoEstoque movimentacao) {
        return new MovimentacaoEstoqueResponse(movimentacao.id().valor(), movimentacao.produtoId().valor(),
                movimentacao.tipo().name(), movimentacao.quantidade().valor(), movimentacao.estoqueAnterior().valor(),
                movimentacao.estoquePosterior().valor(), movimentacao.criadoEm());
    }
}
