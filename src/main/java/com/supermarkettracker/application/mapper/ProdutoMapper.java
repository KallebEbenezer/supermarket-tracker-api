package com.supermarkettracker.application.mapper;
import com.supermarkettracker.application.dto.ProdutoDto;
import com.supermarkettracker.domain.model.Produto;
public final class ProdutoMapper { private ProdutoMapper() { } public static ProdutoDto paraDto(Produto p) { return new ProdutoDto(p.id().valor(), p.empresaId().valor(), p.codigoBarras(), p.nome(), p.precoVenda().valor(), p.estoqueAtual().valor(), p.status().name()); } }
