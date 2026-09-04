package com.supermarkettracker.application.usecase;
import com.supermarkettracker.application.dto.LojaDto;
import com.supermarkettracker.application.mapper.LojaMapper;
import com.supermarkettracker.application.query.BuscarLojaQuery;
import com.supermarkettracker.domain.exception.EntidadeNaoEncontradaException;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.LojaRepository;
public final class BuscarLojaUseCase { private final LojaRepository lojas; public BuscarLojaUseCase(LojaRepository lojas) { this.lojas = lojas; } public LojaDto executar(BuscarLojaQuery q) { return LojaMapper.paraDto(lojas.buscarPorId(new Identificador(q.lojaId())).orElseThrow(() -> new EntidadeNaoEncontradaException("Loja nao encontrada"))); } }
