package com.supermarkettracker.application.usecase;
import com.supermarkettracker.application.query.ExcluirEntidadeQuery;
import com.supermarkettracker.domain.exception.EntidadeNaoEncontradaException;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.UsuarioRepository;
public final class ExcluirUsuarioUseCase { private final UsuarioRepository usuarios; public ExcluirUsuarioUseCase(UsuarioRepository usuarios) { this.usuarios = usuarios; } public void executar(ExcluirEntidadeQuery q) { if (usuarios.buscarPorId(new Identificador(q.entidadeId())).isEmpty()) throw new EntidadeNaoEncontradaException("Usuario nao encontrado"); usuarios.excluirPorId(new Identificador(q.entidadeId())); } }
