package com.supermarkettracker.application.mapper;
import com.supermarkettracker.application.dto.CaixaDto;
import com.supermarkettracker.domain.model.Caixa;
public final class CaixaMapper { private CaixaMapper() { } public static CaixaDto paraDto(Caixa c) { return new CaixaDto(c.id().valor(), c.lojaId().valor(), c.codigo(), c.nome(), c.status().name()); } }
