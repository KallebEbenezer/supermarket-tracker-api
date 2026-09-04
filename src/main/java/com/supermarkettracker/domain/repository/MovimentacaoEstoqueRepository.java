package com.supermarkettracker.domain.repository;

import com.supermarkettracker.domain.model.MovimentacaoEstoque;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import java.util.List;

public interface MovimentacaoEstoqueRepository {
    MovimentacaoEstoque salvar(MovimentacaoEstoque movimentacao);
    List<MovimentacaoEstoque> listarPorProduto(Identificador produtoId);
    List<MovimentacaoEstoque> listarPorEmpresa(Identificador empresaId, Identificador lojaId);
    boolean existeMovimentacaoVenda(Identificador vendaId);
}
