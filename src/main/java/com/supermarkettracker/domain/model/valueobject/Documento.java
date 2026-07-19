package com.supermarkettracker.domain.model.valueobject;

public record Documento(String valor) {
    public Documento {
        if (valor == null || valor.isBlank()) throw new IllegalArgumentException("Documento e obrigatorio");
        valor = valor.replaceAll("\\D", "");
    }
}
