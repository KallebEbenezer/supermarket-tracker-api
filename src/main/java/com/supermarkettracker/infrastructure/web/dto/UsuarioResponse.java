package com.supermarkettracker.infrastructure.web.dto;

import com.supermarkettracker.domain.model.Usuario;
import java.util.UUID;

public record UsuarioResponse(UUID id, UUID authUserId, String nome, String email, String telefone, String status) {
    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(usuario.id().valor(), usuario.authUserId().valor(), usuario.nome(),
                usuario.email().valor(), usuario.telefone(), usuario.status().name());
    }
}
