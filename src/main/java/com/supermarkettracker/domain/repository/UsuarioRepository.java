package com.supermarkettracker.domain.repository;

import com.supermarkettracker.domain.model.Usuario;
import com.supermarkettracker.domain.model.valueobject.Email;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import java.util.Optional;

public interface UsuarioRepository { Usuario salvar(Usuario usuario); Optional<Usuario> buscarPorId(Identificador id); Optional<Usuario> buscarPorEmail(Email email); }
