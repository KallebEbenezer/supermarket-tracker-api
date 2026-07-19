package com.supermarkettracker.domain.model;

import com.supermarkettracker.domain.model.enums.TipoMovimentacaoEstoque;
import com.supermarkettracker.domain.model.valueobject.Dinheiro;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.model.valueobject.Quantidade;
import java.time.Instant;

public record MovimentacaoEstoque(Identificador id, Identificador empresaId, Identificador lojaId,
                                  Identificador produtoId, Identificador usuarioId, Identificador vendaId,
                                  Identificador itemVendaId, TipoMovimentacaoEstoque tipo, Quantidade quantidade,
                                  Quantidade estoqueAnterior, Quantidade estoquePosterior, Dinheiro custoUnitario,
                                  String motivo, String referenciaExterna, Instant criadoEm) { }
