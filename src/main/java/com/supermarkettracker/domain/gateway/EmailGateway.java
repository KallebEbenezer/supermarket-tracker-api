package com.supermarkettracker.domain.gateway;

import com.supermarkettracker.domain.model.valueobject.Email;

/** Porta para envio de e-mails transacionais. */
public interface EmailGateway {
    void enviar(EmailMensagem mensagem);

    record EmailMensagem(Email destinatario, String assunto, String conteudo) { }
}
