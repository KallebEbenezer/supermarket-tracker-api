package com.supermarkettracker.application.usecase;
import com.supermarkettracker.application.command.FecharSessaoCaixaCommand;
import com.supermarkettracker.application.validator.ValidacaoCommand;
import com.supermarkettracker.domain.exception.EntidadeNaoEncontradaException;
import com.supermarkettracker.domain.model.SessaoCaixa;
import com.supermarkettracker.domain.model.enums.StatusSessaoCaixa;
import com.supermarkettracker.domain.model.valueobject.Dinheiro;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.CaixaRepository;
import java.time.Instant;
public final class FecharSessaoCaixaUseCase { private final CaixaRepository caixas; public FecharSessaoCaixaUseCase(CaixaRepository caixas) { this.caixas = caixas; }
    public SessaoCaixa executar(FecharSessaoCaixaCommand c) { ValidacaoCommand.obrigatorio(c.caixaId(), "Caixa"); ValidacaoCommand.obrigatorio(c.usuarioId(), "Usuario"); ValidacaoCommand.naoNegativo(c.valorFechamento(), "Valor de fechamento"); SessaoCaixa aberta = caixas.buscarSessaoAberta(new Identificador(c.caixaId())).orElseThrow(() -> new EntidadeNaoEncontradaException("Sessao de caixa aberta nao encontrada")); return caixas.salvarSessao(new SessaoCaixa(aberta.id(), aberta.caixaId(), aberta.usuarioAberturaId(), new Identificador(c.usuarioId()), aberta.abertoEm(), Instant.now(), aberta.valorAbertura(), new Dinheiro(c.valorFechamento()), StatusSessaoCaixa.FECHADO, c.observacao())); } }
