package com.supermarkettracker.infrastructure.web.request;

import com.supermarkettracker.application.command.CadastrarUsuarioCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record UsuarioRequest(@NotNull UUID authUserId, @NotBlank String nome, @NotBlank @Email String email, String telefone) {
    public CadastrarUsuarioCommand toCommand() { return new CadastrarUsuarioCommand(authUserId, nome, email, telefone); }
}
