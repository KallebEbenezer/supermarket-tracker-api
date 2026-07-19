package com.supermarkettracker.infrastructure.persistence.adapter;

import com.supermarkettracker.domain.model.Produto;
import com.supermarkettracker.domain.model.Venda;
import com.supermarkettracker.infrastructure.persistence.filter.ProdutoFiltro;
import com.supermarkettracker.infrastructure.persistence.filter.VendaFiltro;
import com.supermarkettracker.infrastructure.persistence.mapper.ProdutoPersistenceMapper;
import com.supermarkettracker.infrastructure.persistence.mapper.VendaPersistenceMapper;
import com.supermarkettracker.infrastructure.persistence.repository.PersistenceSpecifications;
import com.supermarkettracker.infrastructure.persistence.repository.ProdutoJpaRepository;
import com.supermarkettracker.infrastructure.persistence.repository.VendaJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

/** Consultas de leitura filtradas; não é uma porta do domínio para não acoplar o domínio ao Spring Data. */
@Repository
public class ConsultaPaginadaJpa {
  private final ProdutoJpaRepository produtos;
  private final VendaJpaRepository vendas;
  private final ProdutoPersistenceMapper produtoMapper;
  private final VendaPersistenceMapper vendaMapper;

  public ConsultaPaginadaJpa(ProdutoJpaRepository produtos, VendaJpaRepository vendas,
      ProdutoPersistenceMapper produtoMapper, VendaPersistenceMapper vendaMapper) {
    this.produtos = produtos; this.vendas = vendas;
    this.produtoMapper = produtoMapper; this.vendaMapper = vendaMapper;
  }

  public Page<Produto> produtos(ProdutoFiltro filtro, Pageable pageable) {
    return produtos.findAll(PersistenceSpecifications.produtos(filtro), pageable).map(produtoMapper::toDomain);
  }

  public Page<Venda> vendas(VendaFiltro filtro, Pageable pageable) {
    return vendas.findAll(PersistenceSpecifications.vendas(filtro), pageable).map(vendaMapper::toDomain);
  }
}
