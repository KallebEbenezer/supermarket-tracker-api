package com.supermarkettracker.application.mapper;

import com.supermarkettracker.application.dto.ContaBancariaDto;
import com.supermarkettracker.domain.model.ContaBancaria;

public final class ContaBancariaMapper {
    private ContaBancariaMapper() { }

    public static ContaBancariaDto paraDto(ContaBancaria c) {
        return new ContaBancariaDto(
            c.id().valor(),
            c.empresaId().valor(),
            c.bancoCodigo(),
            c.bancoNome(),
            c.agencia(),
            c.conta(),
            c.tipo().name(),
            c.titularNome(),
            c.titularDocumento() == null ? null : c.titularDocumento().valor(),
            c.chavePix(),
            c.principal(),
            c.status().name(),
            c.criadoEm(),
            c.atualizadoEm()
        );
    }
}
