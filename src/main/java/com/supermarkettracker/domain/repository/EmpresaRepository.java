package com.supermarkettracker.domain.repository;

import com.supermarkettracker.domain.model.Empresa;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import java.util.List;
import java.util.Optional;

public interface EmpresaRepository { Empresa salvar(Empresa empresa); Optional<Empresa> buscarPorId(Identificador id); List<Empresa> listarPorUsuario(Identificador usuarioId); }
