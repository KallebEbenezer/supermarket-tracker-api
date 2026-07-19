package com.supermarkettracker.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public record DashboardProdutoMaisVendido(UUID produtoId, String produtoNome, BigDecimal quantidadeVendida,
                                          BigDecimal faturamento) { }
