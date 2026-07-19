package com.supermarkettracker.infrastructure.web.request;

import com.supermarkettracker.application.command.IniciarVendaCommand;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

public record VendaRequest(@NotNull UUID empresaId, @NotNull UUID lojaId, UUID sessaoCaixaId, @NotNull UUID usuarioId,
                           UUID clienteId, @Positive long numero, @NotNull @DecimalMin("0.00") BigDecimal subtotal,
                           @NotNull @DecimalMin("0.00") BigDecimal desconto, @NotNull @DecimalMin("0.00") BigDecimal acrescimo,
                           @NotNull @DecimalMin(value = "0.001") BigDecimal quantidadeItens, String observacao) {
    public IniciarVendaCommand toCommand() { return new IniciarVendaCommand(empresaId, lojaId, sessaoCaixaId, usuarioId, clienteId, numero, subtotal, desconto, acrescimo, quantidadeItens, observacao); }
}
