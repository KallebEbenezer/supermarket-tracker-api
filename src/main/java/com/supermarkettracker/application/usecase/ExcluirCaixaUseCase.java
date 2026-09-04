package com.supermarkettracker.application.usecase;
import com.supermarkettracker.application.query.ExcluirEntidadeQuery;
import com.supermarkettracker.domain.exception.EntidadeNaoEncontradaException;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.CaixaRepository;
public final class ExcluirCaixaUseCase { private final CaixaRepository caixas; public ExcluirCaixaUseCase(CaixaRepository caixas) { this.caixas = caixas; } public void executar(ExcluirEntidadeQuery q) { if (caixas.buscarPorId(new Identificador(q.entidadeId())).isEmpty()) throw new EntidadeNaoEncontradaException("Caixa nao encontrado"); caixas.excluirPorId(new Identificador(q.entidadeId())); } }
