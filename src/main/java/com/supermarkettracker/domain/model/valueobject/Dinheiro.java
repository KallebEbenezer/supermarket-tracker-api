package com.supermarkettracker.domain.model.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Dinheiro(BigDecimal valor) {
    public Dinheiro {
        if (valor == null) throw new IllegalArgumentException("Valor monetario e obrigatorio");
        valor = valor.setScale(2, RoundingMode.HALF_UP);
    }
    public static Dinheiro zero() { return new Dinheiro(BigDecimal.ZERO); }
}
