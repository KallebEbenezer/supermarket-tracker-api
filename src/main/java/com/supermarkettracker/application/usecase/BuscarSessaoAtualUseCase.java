package com.supermarkettracker.application.usecase;

import com.supermarkettracker.application.validator.ValidacaoCommand;
import com.supermarkettracker.domain.exception.EntidadeNaoEncontradaException;
import com.supermarkettracker.domain.model.SessaoCaixa;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.CaixaRepository;
import java.util.Optional;
import java.util.UUID;

/** Retorna a sessão de caixa atualmente aberta de um caixa, se houver. */
public final class BuscarSessaoAtualUseCase {
    private final CaixaRepository caixas;

    public BuscarSessaoAtualUseCase(CaixaRepository caixas) {
        this.caixas = caixas;
    }

    public SessaoCaixa executar(UUID caixaId) {
        ValidacaoCommand.obrigatorio(caixaId, "Caixa");
        Optional<SessaoCaixa> aberta = caixas.buscarSessaoAberta(new Identificador(caixaId));
        if (aberta.isEmpty()) {
            throw new EntidadeNaoEncontradaException("Nao existe sessao de caixa aberta para o caixa informado");
        }
        return aberta.get();
    }
}
