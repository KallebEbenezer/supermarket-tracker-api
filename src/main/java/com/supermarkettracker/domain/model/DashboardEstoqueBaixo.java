package com.supermarkettracker.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public record DashboardEstoqueBaixo(UUID produtoId, String produtoNome, BigDecimal estoqueAtual,
                                    BigDecimal estoqueMinimo, String unidadeMedida) { }
