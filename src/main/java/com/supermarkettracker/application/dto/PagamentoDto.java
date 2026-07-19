package com.supermarkettracker.application.dto;
import java.math.BigDecimal;
import java.util.UUID;
public record PagamentoDto(UUID id, UUID vendaId, BigDecimal valor, String tipo, String status, String referencia) { }
