package com.supermarkettracker.infrastructure.web.dto;

import com.supermarkettracker.domain.model.Usuario;
import com.supermarkettracker.domain.model.enums.PapelUsuario;
import java.util.UUID;

/** Dados do usuário autenticado expostos pela API de autenticação. */
public record UsuarioAuthResponse(UUID id, String nome, String email, String telefone, String papel) {
    public static UsuarioAuthResponse from(Usuario usuario, PapelUsuario papel) {
        return new UsuarioAuthResponse(usuario.id().valor(), usuario.nome(), usuario.email().valor(),
                usuario.telefone(), papel == null ? null : papel.name());
    }
}
