package com.supermarkettracker.domain.model;

import java.math.BigDecimal;

public record DashboardResumo(BigDecimal totalVendidoMes, BigDecimal lucroMes, BigDecimal prejuizoMes,
                              BigDecimal ticketMedioMes, int vendasDoDia, int vendasDoMes) { }
