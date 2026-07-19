package com.supermarkettracker.infrastructure.web.dto;

/** Envelope uniforme para respostas bem-sucedidas da API. */
public record ApiResponse<T>(T dados) {
    public static <T> ApiResponse<T> of(T dados) { return new ApiResponse<>(dados); }
}
