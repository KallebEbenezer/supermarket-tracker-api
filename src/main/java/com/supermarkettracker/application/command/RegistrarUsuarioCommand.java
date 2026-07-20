package com.supermarkettracker.application.command;

import com.supermarkettracker.domain.model.enums.PapelUsuario;

public record RegistrarUsuarioCommand(String nome, String email, String senha, String telefone, PapelUsuario papel) {

    public PapelUsuario papel() {
        return papel == null ? PapelUsuario.PROPRIETARIO : papel;
    }
}
