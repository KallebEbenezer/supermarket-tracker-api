package com.supermarkettracker.application.usecase;

import com.supermarkettracker.application.dto.ContaBancariaDto;
import com.supermarkettracker.application.mapper.ContaBancariaMapper;
import com.supermarkettracker.application.query.BuscarContaBancariaQuery;
import com.supermarkettracker.domain.exception.EntidadeNaoEncontradaException;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.ContaBancariaRepository;

public final class BuscarContaBancariaUseCase {
    private final ContaBancariaRepository contas;

    public BuscarContaBancariaUseCase(ContaBancariaRepository contas) {
        this.contas = contas;
    }

    public ContaBancariaDto executar(BuscarContaBancariaQuery q) {
        return ContaBancariaMapper.paraDto(
            contas.buscarPorId(new Identificador(q.contaBancariaId()))
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Conta bancaria nao encontrada"))
        );
    }
}
