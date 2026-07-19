package com.supermarkettracker.infrastructure.integration.mock;

import com.supermarkettracker.domain.gateway.EmailGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmailMockAdapter implements EmailGateway {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailMockAdapter.class);

    @Override
    public void enviar(EmailMensagem mensagem) {
        LOGGER.info("E-mail mock enviado para {}: {}", mensagem.destinatario().valor(), mensagem.assunto());
    }
}
