package com.supermarkettracker.infrastructure.web.request;

import com.supermarkettracker.application.command.RedefinirSenhaCommand;
import jakarta.validation.constraints.NotBlank;

public record RedefinirSenhaRequest(@NotBlank String token, @NotBlank String novaSenha) {
    public RedefinirSenhaCommand toCommand() {
        return new RedefinirSenhaCommand(token, novaSenha);
    }
}
