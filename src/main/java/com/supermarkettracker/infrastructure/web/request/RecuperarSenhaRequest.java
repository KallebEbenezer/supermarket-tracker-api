package com.supermarkettracker.infrastructure.web.request;

import com.supermarkettracker.application.command.SolicitarResetSenhaCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RecuperarSenhaRequest(@NotBlank @Email String email) {
    public SolicitarResetSenhaCommand toCommand() {
        return new SolicitarResetSenhaCommand(email);
    }
}
