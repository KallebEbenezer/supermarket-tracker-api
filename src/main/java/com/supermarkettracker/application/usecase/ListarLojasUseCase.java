package com.supermarkettracker.application.usecase;
import com.supermarkettracker.application.dto.LojaDto;
import com.supermarkettracker.application.mapper.LojaMapper;
import com.supermarkettracker.application.query.ListarLojasQuery;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.LojaRepository;
import java.util.List;
public final class ListarLojasUseCase { private final LojaRepository lojas; public ListarLojasUseCase(LojaRepository lojas) { this.lojas = lojas; } public List<LojaDto> executar(ListarLojasQuery q) { return lojas.listarPorEmpresa(new Identificador(q.empresaId())).stream().map(LojaMapper::paraDto).toList(); } }
