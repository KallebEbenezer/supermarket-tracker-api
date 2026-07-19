package com.supermarkettracker.domain.model;

import com.supermarkettracker.domain.model.valueobject.Dinheiro;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.model.valueobject.Quantidade;
import java.time.Instant;

public record Produto(Identificador id, Identificador empresaId, Identificador categoriaId,
                      String codigoBarras, String sku, String nome, String descricao, String imagemUrl,
                      String unidadeMedida, Dinheiro precoCompra, Dinheiro precoVenda,
                      Quantidade estoqueAtual, Quantidade estoqueMinimo, boolean permiteEstoqueNegativo,
                      StatusProduto status, Instant criadoEm, Instant atualizadoEm) {
    public enum StatusProduto { ATIVO, INATIVO, ARQUIVADO }
}
