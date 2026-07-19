package com.supermarkettracker.infrastructure.web.request;

import com.supermarkettracker.application.command.AdicionarItemVendaCommand;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

public record ItemVendaRequest(UUID produtoId, @Positive int numero, @NotBlank String produtoNome, String codigoBarras,
                               @NotBlank @Size(max = 10) String unidadeMedida,
                               @NotNull @DecimalMin(value = "0.001") BigDecimal quantidade,
                               @NotNull @DecimalMin("0.00") BigDecimal precoUnitario,
                               @NotNull @DecimalMin("0.00") BigDecimal precoCompraUnitario,
                               @NotNull @DecimalMin("0.00") BigDecimal desconto,
                               @NotNull @DecimalMin("0.00") BigDecimal acrescimo) {
    public AdicionarItemVendaCommand toCommand(UUID vendaId) { return new AdicionarItemVendaCommand(vendaId, produtoId, numero, produtoNome, codigoBarras, unidadeMedida, quantidade, precoUnitario, precoCompraUnitario, desconto, acrescimo); }
}
