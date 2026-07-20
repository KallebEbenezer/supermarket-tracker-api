package com.supermarkettracker.infrastructure.web.dto;

/** Resposta de renovação de token: novo access token. */
public record RefreshResponse(String accessToken, long expiresInSegundos) {
}
