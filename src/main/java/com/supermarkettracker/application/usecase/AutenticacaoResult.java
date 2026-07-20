package com.supermarkettracker.application.usecase;

import com.supermarkettracker.domain.model.Usuario;
import com.supermarkettracker.domain.model.enums.PapelUsuario;

/** Resultado de um login/registro bem-sucedido: usuário + tokens de acesso. */
public record AutenticacaoResult(Usuario usuario, PapelUsuario papel, String accessToken, String refreshToken,
                                 long expiresInSegundos) {
}
