package com.supermarkettracker.infrastructure.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class PaymentWebSocketHandler extends TextWebSocketHandler {
    private static final Logger log = LoggerFactory.getLogger(PaymentWebSocketHandler.class);

    private final PaymentWebSocketPublisher publisher;

    public PaymentWebSocketHandler(PaymentWebSocketPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String vendaId = extractVendaId(session);
        if (vendaId != null) {
            publisher.register(vendaId, session);
            log.info("Conexão WebSocket estabelecida para venda {}", vendaId);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String vendaId = extractVendaId(session);
        if (vendaId != null) {
            publisher.unregister(vendaId);
            log.info("Conexão WebSocket fechada para venda {}: {}", vendaId, status);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        log.debug("Mensagem recebida no WebSocket: {}", message.getPayload());
    }

    private String extractVendaId(WebSocketSession session) {
        String path = session.getUri() != null ? session.getUri().getPath() : "";
        String[] parts = path.split("/");
        for (int i = 0; i < parts.length - 1; i++) {
            if ("payment".equals(parts[i]) && i + 1 < parts.length) {
                return parts[i + 1];
            }
        }
        return null;
    }
}
