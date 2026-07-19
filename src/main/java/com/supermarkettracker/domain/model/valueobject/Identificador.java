package com.supermarkettracker.domain.model.valueobject;

import java.util.UUID;

public record Identificador(UUID valor) {
    public Identificador {
        if (valor == null) throw new IllegalArgumentException("Identificador e obrigatorio");
    }
    public static Identificador novo() { return new Identificador(UUID.randomUUID()); }
}
