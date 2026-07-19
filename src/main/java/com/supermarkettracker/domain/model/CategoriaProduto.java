package com.supermarkettracker.domain.model;

import com.supermarkettracker.domain.model.enums.StatusAtivo;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import java.time.Instant;

public record CategoriaProduto(Identificador id, Identificador empresaId, Identificador categoriaPaiId,
                               String nome, StatusAtivo status, Instant criadoEm) { }
