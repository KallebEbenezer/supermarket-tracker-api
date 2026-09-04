package com.supermarkettracker.domain.gateway;

/** Porta para publicação de eventos de pagamento. */
public interface PixPaymentEventPublisher {
    void publish(String vendaId, String status);
}
