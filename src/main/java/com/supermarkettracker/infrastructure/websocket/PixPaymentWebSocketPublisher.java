package com.supermarkettracker.infrastructure.websocket;

import com.supermarkettracker.domain.gateway.PixPaymentEventPublisher;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
public class PixPaymentWebSocketPublisher implements PixPaymentEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(PixPaymentWebSocketPublisher.class);

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void register(String vendaId, WebSocketSession session) {
        sessions.put(vendaId, session);
        log.info("WebSocket registrado para venda {}", vendaId);
    }

    public void unregister(String vendaId) {
        sessions.remove(vendaId);
        log.info("WebSocket removido para venda {}", vendaId);
    }

    @Override
    public void publish(String vendaId, String status) {
        WebSocketSession session = sessions.get(vendaId);
        if (session != null && session.isOpen()) {
            try {
                String payload = "{\"vendaId\":\"" + vendaId + "\",\"status\":\"" + status + "\"}";
                session.sendMessage(new TextMessage(payload));
                log.info("Evento PIX enviado para venda {}: {}", vendaId, status);
            } catch (Exception e) {
                log.error("Erro ao enviar evento PIX para venda {}", vendaId, e);
            }
        } else {
            log.warn("Nenhum WebSocket aberto para venda {}", vendaId);
        }
    }
}
