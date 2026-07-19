package com.supermarkettracker.domain.model;

import com.supermarkettracker.domain.model.enums.StatusPagamento;
import com.supermarkettracker.domain.model.enums.TipoPagamento;
import com.supermarkettracker.domain.model.valueobject.Dinheiro;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import java.time.Instant;

public record Pagamento(Identificador id, Identificador vendaId, Identificador contaBancariaId,
                        TipoPagamento tipo, Dinheiro valor, StatusPagamento status, Instant recebidoEm,
                        String referencia, Instant criadoEm, Instant atualizadoEm) { }
