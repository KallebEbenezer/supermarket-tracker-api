package com.supermarkettracker.infrastructure.web.request;

import com.supermarkettracker.application.command.CadastrarLojaCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;

public record LojaRequest(@NotNull UUID empresaId, @NotBlank String codigo, @NotBlank String nome,
                          @Pattern(regexp = "\\d{14}", message = "cnpj deve conter 14 dígitos") String cnpj,
                          String telefone) {
    public CadastrarLojaCommand toCommand() { return new CadastrarLojaCommand(empresaId, codigo, nome, cnpj, telefone); }
}
