package com.supermarkettracker.infrastructure.web.request;

import com.supermarkettracker.application.command.FecharSessaoCaixaCommand;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record FecharSessaoCaixaRequest(@NotNull UUID usuarioId, @NotNull @DecimalMin("0.00") BigDecimal valorFechamento,
                                       String observacao) {
    public FecharSessaoCaixaCommand toCommand(UUID caixaId) { return new FecharSessaoCaixaCommand(caixaId, usuarioId, valorFechamento, observacao); }
}
