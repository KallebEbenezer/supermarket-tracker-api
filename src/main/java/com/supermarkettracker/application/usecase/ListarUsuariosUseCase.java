package com.supermarkettracker.application.usecase;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.UsuarioRepository;
import com.supermarkettracker.infrastructure.web.dto.UsuarioResponse;
import java.util.List;
public final class ListarUsuariosUseCase { private final UsuarioRepository usuarios; public ListarUsuariosUseCase(UsuarioRepository usuarios) { this.usuarios = usuarios; } public List<UsuarioResponse> executar(ListarUsuariosQuery q) { return usuarios.listarPorEmpresa(new Identificador(q.empresaId())).stream().map(UsuarioResponse::from).toList(); } }
