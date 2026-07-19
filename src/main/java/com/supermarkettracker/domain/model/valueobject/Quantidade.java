package com.supermarkettracker.domain.model.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Quantidade(BigDecimal valor) {
    public Quantidade {
        if (valor == null) throw new IllegalArgumentException("Quantidade e obrigatoria");
        valor = valor.setScale(3, RoundingMode.HALF_UP);
    }
    public static Quantidade zero() { return new Quantidade(BigDecimal.ZERO); }
}
