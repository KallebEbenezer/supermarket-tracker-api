package com.supermarkettracker.application.usecase;

import com.supermarkettracker.application.query.ConsultarDashboardQuery;
import com.supermarkettracker.application.validator.ValidacaoCommand;
import com.supermarkettracker.domain.model.Dashboard;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.DashboardConsultaRepository;

public final class ConsultarDashboardUseCase {
    private final DashboardConsultaRepository dashboard;

    public ConsultarDashboardUseCase(DashboardConsultaRepository dashboard) {
        this.dashboard = dashboard;
    }

    public Dashboard executar(ConsultarDashboardQuery query) {
        ValidacaoCommand.obrigatorio(query.empresaId(), "Empresa");
        int limite = query.limite() == 0 ? 10 : query.limite();
        if (limite < 1 || limite > 100) throw new IllegalArgumentException("Limite deve estar entre 1 e 100");
        return dashboard.consultar(new Identificador(query.empresaId()),
                query.lojaId() == null ? null : new Identificador(query.lojaId()), limite);
    }
}
