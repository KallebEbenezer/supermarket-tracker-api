package com.supermarkettracker.infrastructure.persistence.filter;
import com.supermarkettracker.domain.model.Produto.StatusProduto; import java.util.UUID;
public record ProdutoFiltro(UUID empresaId, String termo, StatusProduto status, UUID categoriaId) {}
