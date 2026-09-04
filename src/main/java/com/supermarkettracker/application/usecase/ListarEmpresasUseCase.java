package com.supermarkettracker.application.usecase;
import com.supermarkettracker.application.dto.EmpresaDto;
import com.supermarkettracker.application.mapper.EmpresaMapper;
import com.supermarkettracker.application.query.ListarEmpresasQuery;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.EmpresaRepository;
import java.util.List;
public final class ListarEmpresasUseCase { private final EmpresaRepository empresas; public ListarEmpresasUseCase(EmpresaRepository empresas) { this.empresas = empresas; } public List<EmpresaDto> executar(ListarEmpresasQuery q) { return empresas.listarPorUsuario(new Identificador(q.usuarioId())).stream().map(EmpresaMapper::paraDto).toList(); } }
