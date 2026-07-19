package com.supermarkettracker.infrastructure.web.dto;

import com.supermarkettracker.domain.model.ItemVenda;
import java.math.BigDecimal;
import java.util.UUID;

public record ItemVendaResponse(UUID id, UUID vendaId, UUID produtoId, int numero, String produtoNome,
                                BigDecimal quantidade, BigDecimal subtotal) {
    public static ItemVendaResponse from(ItemVenda item) {
        return new ItemVendaResponse(item.id().valor(), item.vendaId().valor(),
                item.produtoId() == null ? null : item.produtoId().valor(), item.numero(), item.produtoNome(),
                item.quantidade().valor(), item.subtotal().valor());
    }
}
