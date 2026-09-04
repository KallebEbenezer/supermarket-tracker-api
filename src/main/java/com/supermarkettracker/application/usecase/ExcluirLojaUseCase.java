package com.supermarkettracker.application.usecase;
import com.supermarkettracker.application.query.ExcluirEntidadeQuery;
import com.supermarkettracker.domain.exception.EntidadeNaoEncontradaException;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.LojaRepository;
public final class ExcluirLojaUseCase { private final LojaRepository lojas; public ExcluirLojaUseCase(LojaRepository lojas) { this.lojas = lojas; } public void executar(ExcluirEntidadeQuery q) { if (lojas.buscarPorId(new Identificador(q.entidadeId())).isEmpty()) throw new EntidadeNaoEncontradaException("Loja nao encontrada"); lojas.excluirPorId(new Identificador(q.entidadeId())); } }
