package com.supermarkettracker.domain.model;

import java.util.List;

public record Dashboard(DashboardResumo resumo, List<DashboardUltimaVenda> ultimasVendas,
                        List<DashboardProdutoMaisVendido> produtosMaisVendidos,
                        List<DashboardEstoqueBaixo> estoqueBaixo) { }
