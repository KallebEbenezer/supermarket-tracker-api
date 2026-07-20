package com.supermarkettracker.application.usecase;

import com.supermarkettracker.domain.model.Usuario;
import com.supermarkettracker.domain.model.enums.PapelUsuario;

/** Usuário autenticado atualmente, com seu papel. */
public record UsuarioAtual(Usuario usuario, PapelUsuario papel) {
}
