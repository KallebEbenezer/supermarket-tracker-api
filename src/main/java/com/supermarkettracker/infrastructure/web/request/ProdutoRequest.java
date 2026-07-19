package com.supermarkettracker.infrastructure.web.request;

import com.supermarkettracker.application.command.CadastrarProdutoCommand;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

public record ProdutoRequest(@NotNull UUID empresaId, UUID categoriaId, @NotBlank String codigoBarras, String sku,
                             @NotBlank String nome, String descricao, @Size(max = 10) String unidadeMedida,
                             @NotNull @DecimalMin("0.00") BigDecimal precoCompra,
                             @NotNull @DecimalMin("0.00") BigDecimal precoVenda,
                             @NotNull @DecimalMin("0.000") BigDecimal estoqueMinimo, boolean permiteEstoqueNegativo) {
    public CadastrarProdutoCommand toCommand() { return new CadastrarProdutoCommand(empresaId, categoriaId, codigoBarras, sku, nome, descricao, unidadeMedida, precoCompra, precoVenda, estoqueMinimo, permiteEstoqueNegativo); }
}
