package com.supermarkettracker.infrastructure.web.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.supermarkettracker.application.usecase.PixNotificationService;
import com.supermarkettracker.infrastructure.integration.mercadopago.MercadoPagoWebhookValidator;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class PixWebhookControllerTest {

    private PixNotificationService notificationService;
    private MercadoPagoWebhookValidator validator;
    private HttpClient httpClient;
    private PixWebhookController controller;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() throws Exception {
        notificationService = mock(PixNotificationService.class);
        validator = mock(MercadoPagoWebhookValidator.class);
        httpClient = mock(HttpClient.class);
        controller = new PixWebhookController(notificationService, validator, "test-token",
                "https://api.mercadopago.com", httpClient);
    }

    @Test
    void rejeitaQuandoAssinaturaInvalida() {
        when(validator.validateSignature(any(), any(), any(), any())).thenReturn(false);

        var payload = Map.of("action", "payment.updated",
                "data", Map.of("id", "12345"));
        ResponseEntity<Void> response = controller.handlePixNotification("sig", "req-1", payload);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(notificationService, never()).processarNotificacao(anyString(), anyString());
    }

    @Test
    @SuppressWarnings("unchecked")
    void webhookValidoChamaServiceComStatusAprovado() throws Exception {
        when(validator.validateSignature(any(), any(), eq("12345"), any())).thenReturn(true);

        var httpResponse = mock(HttpResponse.class);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn("{\"status\":\"approved\"}");
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);

        var payload = Map.of("action", "payment.updated",
                "data", Map.of("id", "12345"));
        ResponseEntity<Void> response = controller.handlePixNotification("sig", "req-1", payload);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(notificationService).processarNotificacao("12345", "APROVADO");
    }

    @Test
    @SuppressWarnings("unchecked")
    void webhookComPagamentoRejeitadoMapeiaParaREJEITADO() throws Exception {
        when(validator.validateSignature(any(), any(), any(), any())).thenReturn(true);

        var httpResponse = mock(HttpResponse.class);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn("{\"status\":\"rejected\"}");
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);

        var payload = Map.of("action", "payment.updated",
                "data", Map.of("id", "12345"));
        ResponseEntity<Void> response = controller.handlePixNotification("sig", "req-1", payload);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(notificationService).processarNotificacao("12345", "REJEITADO");
    }

    @Test
    @SuppressWarnings("unchecked")
    void mercadopagoRetorna404_retorna502() throws Exception {
        when(validator.validateSignature(any(), any(), any(), any())).thenReturn(true);

        var httpResponse = mock(HttpResponse.class);
        when(httpResponse.statusCode()).thenReturn(404);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);

        var payload = Map.of("action", "payment.updated",
                "data", Map.of("id", "inexistente"));
        ResponseEntity<Void> response = controller.handlePixNotification("sig", "req-1", payload);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        verify(notificationService, never()).processarNotificacao(anyString(), anyString());
    }

    @Test
    void acaoDiferenteDePaymentUpdatedIgnorada() {
        when(validator.validateSignature(any(), any(), any(), any())).thenReturn(true);

        var payload = Map.<String, Object>of("action", "payment.created");
        ResponseEntity<Void> response = controller.handlePixNotification("sig", "req-1", payload);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(notificationService, never()).processarNotificacao(anyString(), anyString());
    }

    @Test
    void payloadSemDataRetornaBadRequest() {
        when(validator.validateSignature(any(), any(), any(), any())).thenReturn(true);

        var payload = Map.<String, Object>of("action", "payment.updated");
        ResponseEntity<Void> response = controller.handlePixNotification("sig", "req-1", payload);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(notificationService, never()).processarNotificacao(anyString(), anyString());
    }

    @Test
    void tokenAusenteNoMpRetorna502EmVezDeApproved() throws Exception {
        when(validator.validateSignature(any(), any(), any(), any())).thenReturn(true);
        var controllerSemToken = new PixWebhookController(notificationService, validator, "",
                "https://api.mercadopago.com", httpClient);

        var payload = Map.of("action", "payment.updated",
                "data", Map.of("id", "12345"));
        ResponseEntity<Void> response = controllerSemToken.handlePixNotification("sig", "req-1", payload);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        verify(notificationService, never()).processarNotificacao(anyString(), anyString());
    }
}
