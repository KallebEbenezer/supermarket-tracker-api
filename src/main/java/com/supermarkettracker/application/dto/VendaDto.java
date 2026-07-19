package com.supermarkettracker.application.dto;
import java.math.BigDecimal;
import java.util.UUID;
public record VendaDto(UUID id, long numero, BigDecimal total, BigDecimal quantidadeItens, String status) { }
