package com.supermarkettracker.application.usecase;
import com.supermarkettracker.application.dto.CaixaDto;
import com.supermarkettracker.application.mapper.CaixaMapper;
import com.supermarkettracker.application.query.ListarCaixasQuery;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.CaixaRepository;
import java.util.List;
public final class ListarCaixasUseCase { private final CaixaRepository caixas; public ListarCaixasUseCase(CaixaRepository caixas) { this.caixas = caixas; } public List<CaixaDto> executar(ListarCaixasQuery q) { if (q.lojaId() != null) { return caixas.listarPorLoja(new Identificador(q.lojaId())).stream().map(CaixaMapper::paraDto).toList(); } return caixas.listarPorEmpresa(new Identificador(q.empresaId())).stream().map(CaixaMapper::paraDto).toList(); } }
