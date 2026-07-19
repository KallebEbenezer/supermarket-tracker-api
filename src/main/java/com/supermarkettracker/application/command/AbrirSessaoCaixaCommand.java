package com.supermarkettracker.application.command;
import java.math.BigDecimal;
import java.util.UUID;
public record AbrirSessaoCaixaCommand(UUID caixaId, UUID usuarioId, BigDecimal valorAbertura, String observacao) { }
