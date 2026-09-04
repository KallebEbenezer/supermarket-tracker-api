package com.supermarkettracker.infrastructure.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class PixWebSocketHandler extends TextWebSocketHandler {
    private static final Logger log = LoggerFactory.getLogger(PixWebSocketHandler.class);

    private final PixPaymentWebSocketPublisher publisher;

    public PixWebSocketHandler(PixPaymentWebSocketPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String vendaId = extractVendaId(session);
        if (vendaId != null) {
            publisher.register(vendaId, session);
            log.info("Conexão WebSocket PIX estabelecida para venda {}", vendaId);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String vendaId = extractVendaId(session);
        if (vendaId != null) {
            publisher.unregister(vendaId);
            log.info("Conexão WebSocket PIX fechada para venda {}: {}", vendaId, status);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        // Client can send ping/keepalive messages
        log.debug("Mensagem recebida no WebSocket PIX: {}", message.getPayload());
    }

    private String extractVendaId(WebSocketSession session) {
        String path = session.getUri() != null ? session.getUri().getPath() : "";
        String[] parts = path.split("/");
        // Expected: /ws/pix/{vendaId}
        for (int i = 0; i < parts.length - 1; i++) {
            if ("pix".equals(parts[i]) && i + 1 < parts.length) {
                return parts[i + 1];
            }
        }
        return null;
    }
}
