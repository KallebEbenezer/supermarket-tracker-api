package com.supermarkettracker.application.usecase;
import com.supermarkettracker.application.command.CadastrarCaixaCommand;
import com.supermarkettracker.application.validator.ValidacaoCommand;
import com.supermarkettracker.domain.model.Caixa;
import com.supermarkettracker.domain.model.enums.StatusAtivo;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.CaixaRepository;
import java.time.Instant;
public final class CadastrarCaixaUseCase { private final CaixaRepository caixas; public CadastrarCaixaUseCase(CaixaRepository caixas) { this.caixas = caixas; }
    public Caixa executar(CadastrarCaixaCommand c) { ValidacaoCommand.obrigatorio(c.lojaId(), "Loja"); ValidacaoCommand.obrigatorio(c.nome(), "Nome"); Instant agora = Instant.now(); String codigo = (c.codigo() == null || c.codigo().isBlank()) ? gerarCodigo() : c.codigo(); return caixas.salvar(new Caixa(Identificador.novo(), new Identificador(c.lojaId()), codigo, c.nome(), StatusAtivo.ATIVO, agora, agora)); }

    private static String gerarCodigo() { return "CX-" + Identificador.novo().valor().toString().substring(0, 8).toUpperCase(); } }
