package com.supermarkettracker.application.usecase;
import com.supermarkettracker.application.query.ExcluirEntidadeQuery;
import com.supermarkettracker.domain.exception.EntidadeNaoEncontradaException;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.ProdutoRepository;
public final class ExcluirProdutoUseCase { private final ProdutoRepository produtos; public ExcluirProdutoUseCase(ProdutoRepository produtos) { this.produtos = produtos; } public void executar(ExcluirEntidadeQuery q) { if (produtos.buscarPorId(new Identificador(q.entidadeId())).isEmpty()) throw new EntidadeNaoEncontradaException("Produto nao encontrado"); produtos.excluirPorId(new Identificador(q.entidadeId())); } }
