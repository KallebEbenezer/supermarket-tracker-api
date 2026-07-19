package com.supermarkettracker.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemVendaCheckoutCommand(UUID produtoId, int numero, String produtoNome, String codigoBarras,
                                       String unidadeMedida, BigDecimal quantidade, BigDecimal precoUnitario,
                                       BigDecimal precoCompraUnitario, BigDecimal desconto, BigDecimal acrescimo) { }
