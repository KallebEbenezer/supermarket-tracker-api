package com.supermarkettracker.domain.repository;

import com.supermarkettracker.domain.model.ItemVenda;
import com.supermarkettracker.domain.model.Venda;
import java.util.List;

public interface DashboardRepository {
    void registrarVendaPaga(Venda venda, List<ItemVenda> itens);
}
