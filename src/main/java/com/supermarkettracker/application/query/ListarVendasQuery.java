package com.supermarkettracker.application.query;
import java.util.UUID;
public record ListarVendasQuery(UUID empresaId, UUID lojaId, int limite) {
    public ListarVendasQuery {
        if (limite <= 0) limite = 50;
    }
}
