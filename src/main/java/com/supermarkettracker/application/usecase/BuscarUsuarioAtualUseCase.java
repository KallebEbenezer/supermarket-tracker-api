package com.supermarkettracker.application.usecase;

import com.supermarkettracker.domain.exception.EntidadeNaoEncontradaException;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.CredencialRepository;
import com.supermarkettracker.domain.repository.UsuarioRepository;
import java.util.UUID;

/** Retorna o usuário autenticado (e seu papel) a partir do id do token. */
public final class BuscarUsuarioAtualUseCase {
    private final UsuarioRepository usuarios;
    private final CredencialRepository credenciais;

    public BuscarUsuarioAtualUseCase(UsuarioRepository usuarios, CredencialRepository credenciais) {
        this.usuarios = usuarios;
        this.credenciais = credenciais;
    }

    public UsuarioAtual executar(UUID usuarioId) {
        var usuario = usuarios.buscarPorId(new Identificador(usuarioId))
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuario nao encontrado"));
        var credencial = credenciais.buscarPorUsuarioId(new Identificador(usuarioId)).orElse(null);
        return new UsuarioAtual(usuario, credencial == null ? null : credencial.papel());
    }
}
