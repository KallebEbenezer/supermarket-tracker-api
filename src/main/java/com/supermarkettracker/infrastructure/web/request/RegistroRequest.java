package com.supermarkettracker.infrastructure.web.request;

import com.supermarkettracker.application.command.RegistrarUsuarioCommand;
import com.supermarkettracker.domain.model.enums.PapelUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegistroRequest(@NotBlank String nome, @NotBlank @Email String email, @NotBlank String senha,
                              String telefone, PapelUsuario papel) {
    public RegistrarUsuarioCommand toCommand() {
        return new RegistrarUsuarioCommand(nome, email, senha, telefone, papel);
    }
}
