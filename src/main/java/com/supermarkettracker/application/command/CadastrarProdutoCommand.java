package com.supermarkettracker.application.command;
import java.math.BigDecimal;
import java.util.UUID;
public record CadastrarProdutoCommand(UUID empresaId, UUID categoriaId, String codigoBarras, String sku, String nome,
                                      String descricao, String unidadeMedida, BigDecimal precoCompra,
                                      BigDecimal precoVenda, BigDecimal estoqueMinimo, boolean permiteEstoqueNegativo) { }
