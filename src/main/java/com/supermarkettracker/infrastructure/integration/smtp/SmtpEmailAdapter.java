package com.supermarkettracker.infrastructure.integration.smtp;

import com.supermarkettracker.domain.gateway.EmailGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.email.provider", havingValue = "smtp")
public class SmtpEmailAdapter implements EmailGateway {
    private static final Logger log = LoggerFactory.getLogger(SmtpEmailAdapter.class);

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public SmtpEmailAdapter(JavaMailSender mailSender,
            @Value("${app.email.from:noreply@supermarkettracker.com.br}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    @Override
    public void enviar(EmailMensagem mensagem) {
        try {
            var mimeMessage = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(mensagem.destinatario().valor());
            helper.setSubject(mensagem.assunto());
            helper.setText(mensagem.conteudo(), true);
            mailSender.send(mimeMessage);
            log.info("E-mail enviado para {}: {}", mensagem.destinatario().valor(), mensagem.assunto());
        } catch (Exception e) {
            log.warn("E-mail nao enviado para {} (credential nao configurada?): {}",
                    mensagem.destinatario().valor(), e.getMessage());
        }
    }
}
