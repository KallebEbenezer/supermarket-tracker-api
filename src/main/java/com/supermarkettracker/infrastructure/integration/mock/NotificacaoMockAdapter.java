package com.supermarkettracker.infrastructure.integration.mock;

import com.supermarkettracker.domain.gateway.NotificacaoGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NotificacaoMockAdapter implements NotificacaoGateway {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificacaoMockAdapter.class);

    @Override
    public void enviar(Notificacao notificacao) {
        LOGGER.info("Notificação mock enviada para {}: {}", notificacao.destinatarioId().valor(), notificacao.titulo());
    }
}
