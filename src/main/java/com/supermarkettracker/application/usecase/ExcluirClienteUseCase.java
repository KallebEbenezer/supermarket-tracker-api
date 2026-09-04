package com.supermarkettracker.application.usecase;
import com.supermarkettracker.application.query.ExcluirEntidadeQuery;
import com.supermarkettracker.domain.exception.EntidadeNaoEncontradaException;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.ClienteRepository;
public final class ExcluirClienteUseCase { private final ClienteRepository clientes; public ExcluirClienteUseCase(ClienteRepository clientes) { this.clientes = clientes; } public void executar(ExcluirEntidadeQuery q) { if (clientes.buscarPorId(new Identificador(q.entidadeId())).isEmpty()) throw new EntidadeNaoEncontradaException("Cliente nao encontrado"); clientes.excluirPorId(new Identificador(q.entidadeId())); } }
