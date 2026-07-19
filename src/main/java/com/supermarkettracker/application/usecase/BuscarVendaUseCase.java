package com.supermarkettracker.application.usecase;
import com.supermarkettracker.application.dto.VendaDto;
import com.supermarkettracker.application.mapper.VendaMapper;
import com.supermarkettracker.application.query.BuscarVendaQuery;
import com.supermarkettracker.domain.exception.EntidadeNaoEncontradaException;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.VendaRepository;
public final class BuscarVendaUseCase { private final VendaRepository vendas; public BuscarVendaUseCase(VendaRepository vendas) { this.vendas = vendas; } public VendaDto executar(BuscarVendaQuery q) { return VendaMapper.paraDto(vendas.buscarPorId(new Identificador(q.vendaId())).orElseThrow(() -> new EntidadeNaoEncontradaException("Venda nao encontrada"))); } }
