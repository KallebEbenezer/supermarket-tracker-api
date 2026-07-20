package com.supermarkettracker.application.usecase;
import com.supermarkettracker.domain.model.Cliente;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.ClienteRepository;
import com.supermarkettracker.infrastructure.web.dto.ClienteResponse;
import java.util.List;
public final class ListarClientesUseCase { private final ClienteRepository clientes; public ListarClientesUseCase(ClienteRepository clientes) { this.clientes = clientes; } public List<ClienteResponse> executar(ListarClientesQuery q) { return clientes.listarPorEmpresa(new Identificador(q.empresaId())).stream().map(ClienteResponse::from).toList(); } }
