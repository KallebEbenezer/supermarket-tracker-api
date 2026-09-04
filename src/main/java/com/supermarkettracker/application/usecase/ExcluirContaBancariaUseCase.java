package com.supermarkettracker.application.usecase;

import com.supermarkettracker.application.query.ExcluirEntidadeQuery;
import com.supermarkettracker.domain.exception.EntidadeNaoEncontradaException;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.ContaBancariaRepository;

public final class ExcluirContaBancariaUseCase {
    private final ContaBancariaRepository contas;

    public ExcluirContaBancariaUseCase(ContaBancariaRepository contas) {
        this.contas = contas;
    }

    public void executar(ExcluirEntidadeQuery q) {
        if (contas.buscarPorId(new Identificador(q.entidadeId())).isEmpty())
            throw new EntidadeNaoEncontradaException("Conta bancaria nao encontrada");
        contas.excluirPorId(new Identificador(q.entidadeId()));
    }
}
