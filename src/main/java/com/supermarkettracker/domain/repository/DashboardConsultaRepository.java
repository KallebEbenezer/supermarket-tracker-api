package com.supermarkettracker.domain.repository;

import com.supermarkettracker.domain.model.Dashboard;
import com.supermarkettracker.domain.model.valueobject.Identificador;

public interface DashboardConsultaRepository {
    Dashboard consultar(Identificador empresaId, Identificador lojaId, int limite);
}
