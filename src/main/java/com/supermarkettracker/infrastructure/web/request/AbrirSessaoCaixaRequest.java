package com.supermarkettracker.infrastructure.web.request;

import com.supermarkettracker.application.command.AbrirSessaoCaixaCommand;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record AbrirSessaoCaixaRequest(@NotNull UUID usuarioId, @NotNull @DecimalMin("0.00") BigDecimal valorAbertura,
                                      String observacao) {
    public AbrirSessaoCaixaCommand toCommand(UUID caixaId) { return new AbrirSessaoCaixaCommand(caixaId, usuarioId, valorAbertura, observacao); }
}
