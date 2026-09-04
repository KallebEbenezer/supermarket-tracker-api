package com.supermarkettracker.application.command;

import com.supermarkettracker.domain.model.enums.TipoContaBancaria;
import java.util.UUID;

public record CadastrarContaBancariaCommand(
    UUID empresaId,
    String bancoCodigo,
    String bancoNome,
    String agencia,
    String conta,
    TipoContaBancaria tipo,
    String titularNome,
    String titularDocumento,
    String chavePix,
    boolean principal
) { }
