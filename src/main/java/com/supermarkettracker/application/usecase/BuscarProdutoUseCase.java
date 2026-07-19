package com.supermarkettracker.application.usecase;
import com.supermarkettracker.application.dto.ProdutoDto;
import com.supermarkettracker.application.mapper.ProdutoMapper;
import com.supermarkettracker.application.query.BuscarProdutoQuery;
import com.supermarkettracker.domain.exception.EntidadeNaoEncontradaException;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.ProdutoRepository;
public final class BuscarProdutoUseCase { private final ProdutoRepository produtos; public BuscarProdutoUseCase(ProdutoRepository produtos) { this.produtos = produtos; } public ProdutoDto executar(BuscarProdutoQuery q) { return ProdutoMapper.paraDto(produtos.buscarPorId(new Identificador(q.produtoId())).orElseThrow(() -> new EntidadeNaoEncontradaException("Produto nao encontrado"))); } }
