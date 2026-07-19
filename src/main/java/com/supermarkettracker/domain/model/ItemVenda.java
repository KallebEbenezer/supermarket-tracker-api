package com.supermarkettracker.domain.model;

import com.supermarkettracker.domain.model.valueobject.Dinheiro;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.model.valueobject.Quantidade;
import java.time.Instant;

public record ItemVenda(Identificador id, Identificador vendaId, Identificador produtoId, int numero,
                        String produtoNome, String codigoBarras, String unidadeMedida, Quantidade quantidade,
                        Dinheiro precoUnitario, Dinheiro precoCompraUnitario, Dinheiro desconto,
                        Dinheiro acrescimo, Dinheiro subtotal, Dinheiro custoTotal, Dinheiro lucro,
                        Instant criadoEm) { }
