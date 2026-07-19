package com.supermarkettracker.application.command;
import com.supermarkettracker.domain.model.enums.TipoMovimentacaoEstoque;
import java.math.BigDecimal;
import java.util.UUID;
public record RegistrarMovimentacaoEstoqueCommand(UUID empresaId, UUID lojaId, UUID produtoId, UUID usuarioId,
                                                  UUID vendaId, UUID itemVendaId, TipoMovimentacaoEstoque tipo,
                                                  BigDecimal quantidade, BigDecimal custoUnitario, String motivo,
                                                  String referenciaExterna) { }
