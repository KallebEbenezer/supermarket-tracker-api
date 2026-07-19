package com.supermarkettracker.application.mapper;
import com.supermarkettracker.application.dto.LojaDto;
import com.supermarkettracker.domain.model.Loja;
public final class LojaMapper { private LojaMapper() { } public static LojaDto paraDto(Loja l) { return new LojaDto(l.id().valor(), l.empresaId().valor(), l.codigo(), l.nome(), l.status().name()); } }
