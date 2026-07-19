package com.supermarkettracker.infrastructure.web.dto;

import com.supermarkettracker.domain.model.Dashboard;
import com.supermarkettracker.domain.model.DashboardEstoqueBaixo;
import com.supermarkettracker.domain.model.DashboardProdutoMaisVendido;
import com.supermarkettracker.domain.model.DashboardResumo;
import com.supermarkettracker.domain.model.DashboardUltimaVenda;
import java.util.List;

public record DashboardResponse(DashboardResumo resumo, List<DashboardUltimaVenda> ultimasVendas,
                                List<DashboardProdutoMaisVendido> produtosMaisVendidos,
                                List<DashboardEstoqueBaixo> estoqueBaixo) {
    public static DashboardResponse from(Dashboard dashboard) {
        return new DashboardResponse(dashboard.resumo(), dashboard.ultimasVendas(), dashboard.produtosMaisVendidos(),
                dashboard.estoqueBaixo());
    }
}
