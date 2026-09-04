package com.supermarkettracker.infrastructure.web.dto;

/** Resposta de login/registro: tokens de acesso + dados do usuário. */
public record AuthResponse(String accessToken, String refreshToken, long expiresInSegundos,
                           UsuarioAuthResponse usuario, String empresaId, String lojaId) {
}
