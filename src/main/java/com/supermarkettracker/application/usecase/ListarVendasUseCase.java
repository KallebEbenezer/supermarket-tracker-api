package com.supermarkettracker.application.usecase;
import com.supermarkettracker.application.dto.VendaDto;
import com.supermarkettracker.application.mapper.VendaMapper;
import com.supermarkettracker.application.query.ListarVendasQuery;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.VendaRepository;
import java.util.List;
public final class ListarVendasUseCase { private final VendaRepository vendas; public ListarVendasUseCase(VendaRepository vendas) { this.vendas = vendas; } public List<VendaDto> executar(ListarVendasQuery q) { return vendas.listarPorEmpresa(new Identificador(q.empresaId()), q.lojaId() == null ? null : new Identificador(q.lojaId()), q.limite()).stream().map(VendaMapper::paraDto).toList(); } }
