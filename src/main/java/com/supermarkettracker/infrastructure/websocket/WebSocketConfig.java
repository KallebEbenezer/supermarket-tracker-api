package com.supermarkettracker.infrastructure.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final PaymentWebSocketHandler paymentWebSocketHandler;
    private final PixWebSocketHandler pixWebSocketHandler;

    public WebSocketConfig(PaymentWebSocketHandler paymentWebSocketHandler,
                           PixWebSocketHandler pixWebSocketHandler) {
        this.paymentWebSocketHandler = paymentWebSocketHandler;
        this.pixWebSocketHandler = pixWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(paymentWebSocketHandler, "/ws/payment/{vendaId}")
                .setAllowedOrigins("*");
        registry.addHandler(pixWebSocketHandler, "/ws/pix/{vendaId}")
                .setAllowedOrigins("*");
    }
}
