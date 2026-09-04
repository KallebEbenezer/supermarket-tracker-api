package com.supermarkettracker.infrastructure.web.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.supermarkettracker.application.usecase.PixNotificationService;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

class PixMockConfirmControllerTest {

    @Test
    void mockConfirmChamaServiceComStatusPadraoAprovado() {
        var notificationService = mock(PixNotificationService.class);
        var controller = new PixMockConfirmController(notificationService);

        ResponseEntity<Map<String, Object>> response = controller.mockConfirm("tx-123", "APROVADO");

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody())
                .containsEntry("status", "ok")
                .containsEntry("transacaoId", "tx-123")
                .containsEntry("novoStatus", "APROVADO");
        verify(notificationService).processarNotificacao("tx-123", "APROVADO");
    }

    @Test
    void mockConfirmComStatusPersonalizado() {
        var notificationService = mock(PixNotificationService.class);
        var controller = new PixMockConfirmController(notificationService);

        ResponseEntity<Map<String, Object>> response = controller.mockConfirm("tx-456", "REJEITADO");

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody())
                .containsEntry("status", "ok")
                .containsEntry("transacaoId", "tx-456")
                .containsEntry("novoStatus", "REJEITADO");
        verify(notificationService).processarNotificacao("tx-456", "REJEITADO");
    }
}
