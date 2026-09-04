package com.supermarkettracker.application.usecase;

import com.supermarkettracker.application.command.CadastrarContaBancariaCommand;
import com.supermarkettracker.application.dto.ContaBancariaDto;
import com.supermarkettracker.application.mapper.ContaBancariaMapper;
import com.supermarkettracker.application.validator.ChavePixValidator;
import com.supermarkettracker.application.validator.ValidacaoCommand;
import com.supermarkettracker.domain.exception.EntidadeNaoEncontradaException;
import com.supermarkettracker.domain.model.ContaBancaria;
import com.supermarkettracker.domain.model.enums.StatusAtivo;
import com.supermarkettracker.domain.model.valueobject.Documento;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.ContaBancariaRepository;
import java.time.Instant;

public final class CadastrarContaBancariaUseCase {
    private final ContaBancariaRepository contas;

    public CadastrarContaBancariaUseCase(ContaBancariaRepository contas) {
        this.contas = contas;
    }

    public ContaBancariaDto executar(CadastrarContaBancariaCommand c) {
        ValidacaoCommand.obrigatorio(c.empresaId(), "Empresa");
        ValidacaoCommand.obrigatorio(c.bancoCodigo(), "Codigo do banco");
        ValidacaoCommand.obrigatorio(c.bancoNome(), "Nome do banco");
        ValidacaoCommand.obrigatorio(c.tipo(), "Tipo da conta");
        ValidacaoCommand.obrigatorio(c.titularNome(), "Nome do titular");

        Instant agora = Instant.now();
        String chavePix = normalizarChavePix(c.chavePix());
        ContaBancaria conta = new ContaBancaria(
            Identificador.novo(),
            new Identificador(c.empresaId()),
            c.bancoCodigo(),
            c.bancoNome(),
            c.agencia(),
            c.conta(),
            c.tipo(),
            c.titularNome(),
            c.titularDocumento() == null || c.titularDocumento().isBlank() ? null : new Documento(c.titularDocumento()),
            chavePix,
            c.principal(),
            StatusAtivo.ATIVO,
            agora,
            agora
        );

        return ContaBancariaMapper.paraDto(contas.salvar(conta));
    }

    public ContaBancariaDto executarAtualizacao(Identificador id, CadastrarContaBancariaCommand c) {
        ContaBancaria existente = contas.buscarPorId(id)
            .orElseThrow(() -> new EntidadeNaoEncontradaException("Conta bancaria nao encontrada"));

        Instant agora = Instant.now();
        String chavePix = normalizarChavePix(c.chavePix());
        ContaBancaria atualizada = new ContaBancaria(
            existente.id(),
            existente.empresaId(),
            c.bancoCodigo(),
            c.bancoNome(),
            c.agencia(),
            c.conta(),
            c.tipo(),
            c.titularNome(),
            c.titularDocumento() == null || c.titularDocumento().isBlank() ? null : new Documento(c.titularDocumento()),
            chavePix,
            c.principal(),
            existente.status(),
            existente.criadoEm(),
            agora
        );

        return ContaBancariaMapper.paraDto(contas.salvar(atualizada));
    }

    /** A conta pode existir sem chave PIX; quando informada, é validada e normalizada. */
    private String normalizarChavePix(String chavePix) {
        if (chavePix == null || chavePix.isBlank()) {
            return null;
        }
        return ChavePixValidator.validar(chavePix);
    }
}
