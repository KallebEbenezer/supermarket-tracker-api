package com.supermarkettracker.infrastructure.web.request;

import com.supermarkettracker.application.command.RegistrarMovimentacaoEstoqueCommand;
import com.supermarkettracker.domain.model.enums.TipoMovimentacaoEstoque;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record MovimentacaoEstoqueRequest(@NotNull UUID empresaId, @NotNull UUID lojaId, @NotNull UUID produtoId,
                                         @NotNull UUID usuarioId, UUID vendaId, UUID itemVendaId,
                                         @NotNull TipoMovimentacaoEstoque tipo,
                                         @NotNull @DecimalMin(value = "0.001") BigDecimal quantidade,
                                         @DecimalMin("0.00") BigDecimal custoUnitario, String motivo, String referenciaExterna) {
    public RegistrarMovimentacaoEstoqueCommand toCommand() { return new RegistrarMovimentacaoEstoqueCommand(empresaId, lojaId, produtoId, usuarioId, vendaId, itemVendaId, tipo, quantidade, custoUnitario, motivo, referenciaExterna); }
}
