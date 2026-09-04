package com.supermarkettracker.domain.repository;

import com.supermarkettracker.domain.model.Usuario;
import com.supermarkettracker.domain.model.valueobject.Email;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import java.util.Optional;
import java.util.List;

public interface UsuarioRepository { Usuario salvar(Usuario usuario); Optional<Usuario> buscarPorId(Identificador id); Optional<Usuario> buscarPorEmail(Email email); List<Usuario> listarPorEmpresa(Identificador empresaId); void excluirPorId(Identificador id); }
