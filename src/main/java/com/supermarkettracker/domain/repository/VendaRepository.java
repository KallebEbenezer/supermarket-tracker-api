package com.supermarkettracker.domain.repository;

import com.supermarkettracker.domain.model.ItemVenda;
import com.supermarkettracker.domain.model.MovimentacaoEstoque;
import com.supermarkettracker.domain.model.Venda;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import java.util.List;
import java.util.Optional;

public interface VendaRepository {
    Venda salvar(Venda venda);
    Optional<Venda> buscarPorId(Identificador id);
    ItemVenda salvarItem(ItemVenda item);
    /** Persiste o item e a respectiva baixa de estoque na mesma transação. */
    ItemVenda salvarItemComBaixaEstoque(ItemVenda item, MovimentacaoEstoque movimentacao);
    List<ItemVenda> listarItens(Identificador vendaId);
}
