package com.supermarkettracker.application.usecase;
import com.supermarkettracker.application.dto.ProdutoDto;
import com.supermarkettracker.application.mapper.ProdutoMapper;
import com.supermarkettracker.application.query.ListarProdutosQuery;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.ProdutoRepository;
import java.util.List;
public final class ListarProdutosUseCase { private final ProdutoRepository produtos; public ListarProdutosUseCase(ProdutoRepository produtos) { this.produtos = produtos; } public List<ProdutoDto> executar(ListarProdutosQuery q) { return produtos.listarPorEmpresa(new Identificador(q.empresaId())).stream().map(ProdutoMapper::paraDto).toList(); } }
