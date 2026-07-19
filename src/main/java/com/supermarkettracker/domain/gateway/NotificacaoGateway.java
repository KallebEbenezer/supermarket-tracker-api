package com.supermarkettracker.domain.gateway;

import com.supermarkettracker.domain.model.valueobject.Identificador;

/** Porta para notificações assíncronas/in-app ou push. */
public interface NotificacaoGateway {
    void enviar(Notificacao notificacao);

    record Notificacao(Identificador destinatarioId, String titulo, String mensagem, String tipo) { }
}
