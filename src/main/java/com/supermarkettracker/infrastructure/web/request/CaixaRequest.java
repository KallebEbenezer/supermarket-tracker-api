package com.supermarkettracker.infrastructure.web.request;

import com.supermarkettracker.application.command.CadastrarCaixaCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CaixaRequest(@NotNull UUID lojaId, String codigo, @NotBlank String nome) {
    public CadastrarCaixaCommand toCommand() { return new CadastrarCaixaCommand(lojaId, codigo, nome); }
}
