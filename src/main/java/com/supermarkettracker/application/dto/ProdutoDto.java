package com.supermarkettracker.application.dto;
import java.math.BigDecimal;
import java.util.UUID;
public record ProdutoDto(UUID id, UUID empresaId, String codigoBarras, String nome, BigDecimal precoVenda,
                         BigDecimal estoqueAtual, String status) { }
