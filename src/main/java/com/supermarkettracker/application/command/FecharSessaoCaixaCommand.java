package com.supermarkettracker.application.command;
import java.math.BigDecimal;
import java.util.UUID;
public record FecharSessaoCaixaCommand(UUID caixaId, UUID usuarioId, BigDecimal valorFechamento, String observacao) { }
