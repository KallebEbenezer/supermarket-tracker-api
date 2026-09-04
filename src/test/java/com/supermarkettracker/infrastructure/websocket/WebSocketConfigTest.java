package com.supermarkettracker.infrastructure.websocket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistration;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

class WebSocketConfigTest {

    @Test
    void registraAmbosOsHandlersPaymentEPix() {
        var paymentHandler = mock(PaymentWebSocketHandler.class);
        var pixHandler = mock(PixWebSocketHandler.class);
        var config = new WebSocketConfig(paymentHandler, pixHandler);
        var registry = mock(WebSocketHandlerRegistry.class);

        var registration = mock(WebSocketHandlerRegistration.class);
        when(registration.setAllowedOrigins(any())).thenReturn(registration);
        when(registry.addHandler(any(), anyString())).thenReturn(registration);

        config.registerWebSocketHandlers(registry);

        verify(registry).addHandler(paymentHandler, "/ws/payment/{vendaId}");
        verify(registry).addHandler(pixHandler, "/ws/pix/{vendaId}");
        verify(registration, times(2)).setAllowedOrigins("*");
    }
}