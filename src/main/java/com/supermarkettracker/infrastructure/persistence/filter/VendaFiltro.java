package com.supermarkettracker.infrastructure.persistence.filter;
import com.supermarkettracker.domain.model.enums.StatusVenda; import java.time.Instant; import java.util.UUID;
public record VendaFiltro(UUID empresaId, UUID lojaId, UUID clienteId, StatusVenda status, Instant inicio, Instant fim) {}
