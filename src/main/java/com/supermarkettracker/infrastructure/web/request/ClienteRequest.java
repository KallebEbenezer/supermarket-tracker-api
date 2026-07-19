package com.supermarkettracker.infrastructure.web.request;

import com.supermarkettracker.application.command.CadastrarClienteCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

public record ClienteRequest(@NotNull UUID empresaId, @NotBlank String nome, String cpfCnpj, @Email String email,
                             String telefone, LocalDate dataNascimento) {
    public CadastrarClienteCommand toCommand() { return new CadastrarClienteCommand(empresaId, nome, cpfCnpj, email, telefone, dataNascimento); }
}
