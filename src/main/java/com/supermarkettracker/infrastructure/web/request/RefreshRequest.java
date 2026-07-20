package com.supermarkettracker.infrastructure.web.request;

import com.supermarkettracker.application.command.RenovarTokenCommand;
import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(@NotBlank String refreshToken) {
    public RenovarTokenCommand toCommand() {
        return new RenovarTokenCommand(refreshToken);
    }
}
