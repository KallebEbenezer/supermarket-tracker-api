package com.supermarkettracker.application.usecase;
import com.supermarkettracker.application.command.AbrirSessaoCaixaCommand;
import com.supermarkettracker.application.validator.ValidacaoCommand;
import com.supermarkettracker.domain.exception.ConflitoDeDominioException;
import com.supermarkettracker.domain.model.SessaoCaixa;
import com.supermarkettracker.domain.model.enums.StatusSessaoCaixa;
import com.supermarkettracker.domain.model.valueobject.Dinheiro;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.CaixaRepository;
import java.time.Instant;
public final class AbrirSessaoCaixaUseCase { private final CaixaRepository caixas; public AbrirSessaoCaixaUseCase(CaixaRepository caixas) { this.caixas = caixas; }
    public SessaoCaixa executar(AbrirSessaoCaixaCommand c) { ValidacaoCommand.obrigatorio(c.caixaId(), "Caixa"); ValidacaoCommand.obrigatorio(c.usuarioId(), "Usuario"); ValidacaoCommand.naoNegativo(c.valorAbertura(), "Valor de abertura"); if (caixas.buscarSessaoAberta(new Identificador(c.caixaId())).isPresent()) throw new ConflitoDeDominioException("Caixa ja possui sessao aberta"); return caixas.salvarSessao(new SessaoCaixa(Identificador.novo(), new Identificador(c.caixaId()), new Identificador(c.usuarioId()), null, Instant.now(), null, new Dinheiro(c.valorAbertura()), null, StatusSessaoCaixa.ABERTO, c.observacao())); } }
