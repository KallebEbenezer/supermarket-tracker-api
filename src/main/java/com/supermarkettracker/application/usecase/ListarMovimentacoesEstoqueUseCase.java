package com.supermarkettracker.application.usecase;
import com.supermarkettracker.domain.model.MovimentacaoEstoque;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.MovimentacaoEstoqueRepository;
import com.supermarkettracker.infrastructure.web.dto.MovimentacaoEstoqueResponse;
import java.util.List;
import com.supermarkettracker.application.query.ListarMovimentacoesEstoqueQuery;
public final class ListarMovimentacoesEstoqueUseCase { private final MovimentacaoEstoqueRepository movimentacoes; public ListarMovimentacoesEstoqueUseCase(MovimentacaoEstoqueRepository movimentacoes) { this.movimentacoes = movimentacoes; } public List<MovimentacaoEstoqueResponse> executar(ListarMovimentacoesEstoqueQuery q) { return movimentacoes.listarPorEmpresa(new Identificador(q.empresaId()), q.lojaId() == null ? null : new Identificador(q.lojaId())).stream().map(MovimentacaoEstoqueResponse::from).toList(); } }
