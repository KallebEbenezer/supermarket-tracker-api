package com.supermarkettracker.domain.repository;

import com.supermarkettracker.domain.model.Produto;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import java.util.List;
import java.util.Optional;

public interface ProdutoRepository { Produto salvar(Produto produto); Optional<Produto> buscarPorId(Identificador id); Optional<Produto> buscarPorCodigoBarras(Identificador empresaId, String codigoBarras); List<Produto> buscarPorNome(Identificador empresaId, String nome); List<Produto> listarPorEmpresa(Identificador empresaId); }
