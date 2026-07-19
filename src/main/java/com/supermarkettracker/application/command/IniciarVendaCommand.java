package com.supermarkettracker.application.command;
import java.math.BigDecimal;
import java.util.UUID;
public record IniciarVendaCommand(UUID empresaId, UUID lojaId, UUID sessaoCaixaId, UUID usuarioId, UUID clienteId,
                                  long numero, BigDecimal subtotal, BigDecimal desconto, BigDecimal acrescimo,
                                  BigDecimal quantidadeItens, String observacao) { }
