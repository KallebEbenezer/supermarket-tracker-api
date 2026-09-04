package com.supermarkettracker.domain.repository;

import com.supermarkettracker.domain.model.ContaBancaria;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import java.util.List;
import java.util.Optional;

public interface ContaBancariaRepository {
    ContaBancaria salvar(ContaBancaria conta);
    Optional<ContaBancaria> buscarPorId(Identificador id);
    List<ContaBancaria> listarPorEmpresa(Identificador empresaId);
    void excluirPorId(Identificador id);
}
