package com.supermarkettracker.domain.repository;

import com.supermarkettracker.domain.model.Cliente;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import java.util.Optional;
import java.util.List;

public interface ClienteRepository { Cliente salvar(Cliente cliente); Optional<Cliente> buscarPorId(Identificador id); List<Cliente> listarPorEmpresa(Identificador empresaId); void excluirPorId(Identificador id); }
