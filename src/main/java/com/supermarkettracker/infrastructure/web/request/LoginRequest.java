package com.supermarkettracker.infrastructure.web.request;

import com.supermarkettracker.application.command.AutenticarUsuarioCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank @Email String email, @NotBlank String senha) {
    public AutenticarUsuarioCommand toCommand() {
        return new AutenticarUsuarioCommand(email, senha);
    }
}
