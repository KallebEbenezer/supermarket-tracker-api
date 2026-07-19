package com.supermarkettracker.domain.model;

import com.supermarkettracker.domain.model.valueobject.Dinheiro;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.model.valueobject.Quantidade;
import java.time.Instant;
import java.time.LocalDate;

public record DashboardCacheDiario(Identificador id, Identificador empresaId, Identificador lojaId,
                                   LocalDate dataReferencia, int quantidadeVendas, Dinheiro faturamento,
                                   Dinheiro descontos, Dinheiro lucro, Dinheiro prejuizo, Dinheiro ticketMedio,
                                   Quantidade produtosVendidos, Instant ultimaAtualizacao, long versao) { }
