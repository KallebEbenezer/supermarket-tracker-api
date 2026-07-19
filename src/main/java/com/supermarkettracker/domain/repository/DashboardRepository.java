package com.supermarkettracker.domain.repository;

import com.supermarkettracker.domain.model.Venda;

public interface DashboardRepository {
    void registrarVendaPaga(Venda venda);
}
