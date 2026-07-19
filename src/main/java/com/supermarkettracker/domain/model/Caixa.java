package com.supermarkettracker.domain.model;

import com.supermarkettracker.domain.model.enums.StatusAtivo;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import java.time.Instant;

public record Caixa(Identificador id, Identificador lojaId, String codigo, String nome,
                    StatusAtivo status, Instant criadoEm, Instant atualizadoEm) { }
