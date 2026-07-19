package com.supermarkettracker.infrastructure.web.request;

import com.supermarkettracker.application.command.CadastrarEmpresaCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record EmpresaRequest(@NotBlank String razaoSocial, @NotBlank String nomeFantasia,
                             @Pattern(regexp = "\\d{14}", message = "cnpj deve conter 14 dígitos") String cnpj) {
    public CadastrarEmpresaCommand toCommand() { return new CadastrarEmpresaCommand(razaoSocial, nomeFantasia, cnpj); }
}
