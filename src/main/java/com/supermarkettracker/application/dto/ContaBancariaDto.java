package com.supermarkettracker.application.dto;

import java.time.Instant;
import java.util.UUID;

public record ContaBancariaDto(
    UUID id,
    UUID empresaId,
    String bancoCodigo,
    String bancoNome,
    String agencia,
    String conta,
    String tipo,
    String titularNome,
    String titularDocumento,
    String chavePix,
    boolean principal,
    String status,
    Instant criadoEm,
    Instant atualizadoEm
) { }
