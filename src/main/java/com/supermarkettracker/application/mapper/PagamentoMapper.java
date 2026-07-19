package com.supermarkettracker.application.mapper;
import com.supermarkettracker.application.dto.PagamentoDto;
import com.supermarkettracker.domain.model.Pagamento;
public final class PagamentoMapper { private PagamentoMapper() { } public static PagamentoDto paraDto(Pagamento p) { return new PagamentoDto(p.id().valor(), p.vendaId().valor(), p.valor().valor(), p.tipo().name(), p.status().name(), p.referencia()); } }
