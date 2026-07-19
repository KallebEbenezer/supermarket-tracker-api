package com.supermarkettracker.domain.model;

import com.supermarkettracker.domain.model.enums.StatusVenda;
import com.supermarkettracker.domain.model.valueobject.Dinheiro;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.model.valueobject.Quantidade;
import java.time.Instant;

public record Venda(Identificador id, Identificador empresaId, Identificador lojaId, Identificador sessaoCaixaId,
                    Identificador usuarioId, Identificador clienteId, long numero, Dinheiro subtotal,
                    Dinheiro desconto, Dinheiro acrescimo, Dinheiro total, Dinheiro custoTotal,
                    Dinheiro lucro, Dinheiro prejuizo, Quantidade quantidadeItens, StatusVenda status,
                    Instant finalizadaEm, Instant canceladaEm, String motivoCancelamento, String observacao,
                    Instant criadoEm, Instant atualizadoEm) { }
