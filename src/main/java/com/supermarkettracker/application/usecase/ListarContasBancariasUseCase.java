package com.supermarkettracker.application.usecase;

import com.supermarkettracker.application.dto.ContaBancariaDto;
import com.supermarkettracker.application.mapper.ContaBancariaMapper;
import com.supermarkettracker.application.query.ListarContasBancariasQuery;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.ContaBancariaRepository;
import java.util.List;

public final class ListarContasBancariasUseCase {
    private final ContaBancariaRepository contas;

    public ListarContasBancariasUseCase(ContaBancariaRepository contas) {
        this.contas = contas;
    }

    public List<ContaBancariaDto> executar(ListarContasBancariasQuery q) {
        return contas.listarPorEmpresa(new Identificador(q.empresaId()))
            .stream().map(ContaBancariaMapper::paraDto).toList();
    }
}
