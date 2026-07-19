package com.supermarkettracker.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record DashboardUltimaVenda(UUID vendaId, long numero, BigDecimal total, Instant finalizadaEm) { }
