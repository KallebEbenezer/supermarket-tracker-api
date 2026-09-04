package com.supermarkettracker.infrastructure.web.request;

import com.supermarkettracker.application.command.CadastrarContaBancariaCommand;
import com.supermarkettracker.domain.model.enums.TipoContaBancaria;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ContaBancariaRequest(
    @NotNull UUID empresaId,
    @NotBlank String bancoCodigo,
    @NotBlank String bancoNome,
    String agencia,
    String conta,
    @NotNull TipoContaBancaria tipo,
    @NotBlank String titularNome,
    String titularDocumento,
    String chavePix,
    boolean principal
) {
    public CadastrarContaBancariaCommand toCommand() {
        return new CadastrarContaBancariaCommand(empresaId, bancoCodigo, bancoNome, agencia, conta, tipo, titularNome, titularDocumento, chavePix, principal);
    }
}
