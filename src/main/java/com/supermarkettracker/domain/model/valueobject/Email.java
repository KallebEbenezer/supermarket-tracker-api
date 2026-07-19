package com.supermarkettracker.domain.model.valueobject;

public record Email(String valor) {
    public Email {
        if (valor == null || valor.isBlank() || !valor.contains("@")) {
            throw new IllegalArgumentException("E-mail invalido");
        }
        valor = valor.trim().toLowerCase();
    }
}
