package com.supermarkettracker.application.command;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record FinalizarVendaCommand(UUID empresaId, UUID lojaId, UUID sessaoCaixaId, UUID usuarioId,
                                    UUID clienteId, Long numero, BigDecimal desconto, BigDecimal acrescimo,
                                    String observacao, List<ItemVendaCheckoutCommand> itens,
                                    List<PagamentoCheckoutCommand> pagamentos) { }
