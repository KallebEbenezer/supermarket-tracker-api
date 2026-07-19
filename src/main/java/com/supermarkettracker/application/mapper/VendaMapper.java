package com.supermarkettracker.application.mapper;
import com.supermarkettracker.application.dto.VendaDto;
import com.supermarkettracker.domain.model.Venda;
public final class VendaMapper { private VendaMapper() { } public static VendaDto paraDto(Venda v) { return new VendaDto(v.id().valor(), v.numero(), v.total().valor(), v.quantidadeItens().valor(), v.status().name()); } }
