package com.supermarkettracker.domain.repository;

import com.supermarkettracker.domain.model.ItemVenda;
import com.supermarkettracker.domain.model.Venda;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import java.util.List;
import java.util.Optional;

public interface VendaRepository {
    Venda salvar(Venda venda);
    Optional<Venda> buscarPorId(Identificador id);
    ItemVenda salvarItem(ItemVenda item);
    List<ItemVenda> listarItens(Identificador vendaId);
    List<Venda> listarPorEmpresa(Identificador empresaId, Identificador lojaId, int limite);
    Optional<Long> findMaxNumeroByEmpresaId(Identificador empresaId);
}
