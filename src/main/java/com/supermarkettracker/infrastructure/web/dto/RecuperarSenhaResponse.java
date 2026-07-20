package com.supermarkettracker.infrastructure.web.dto;

/** Resposta de solicitação de redefinição. Em dev, retorna o token gerado. */
public record RecuperarSenhaResponse(String token) {
}
