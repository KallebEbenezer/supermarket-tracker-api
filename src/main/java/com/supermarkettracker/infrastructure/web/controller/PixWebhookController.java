package com.supermarkettracker.infrastructure.web.controller;

import com.supermarkettracker.application.usecase.PixNotificationService;
import com.supermarkettracker.infrastructure.integration.mercadopago.MercadoPagoWebhookValidator;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Tag(name = "PIX Webhooks")
@RestController
@RequestMapping("/api/v1/webhooks")
public class PixWebhookController {
    private static final Logger log = LoggerFactory.getLogger(PixWebhookController.class);

    private final PixNotificationService notificationService;
    private final MercadoPagoWebhookValidator webhookValidator;
    private final String mercadopagoAccessToken;
    private final String mercadopagoBaseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public PixWebhookController(PixNotificationService notificationService,
                                MercadoPagoWebhookValidator webhookValidator,
                                @Value("${app.mercadopago.access-token:}") String mercadopagoAccessToken,
                                @Value("${app.mercadopago.base-url:https://api.mercadopago.com}") String mercadopagoBaseUrl) {
        this(notificationService, webhookValidator, mercadopagoAccessToken, mercadopagoBaseUrl, HttpClient.newHttpClient());
    }

    // Package-private for testing
    PixWebhookController(PixNotificationService notificationService,
                         MercadoPagoWebhookValidator webhookValidator,
                         String mercadopagoAccessToken,
                         String mercadopagoBaseUrl,
                         HttpClient httpClient) {
        this.notificationService = notificationService;
        this.webhookValidator = webhookValidator;
        this.mercadopagoAccessToken = mercadopagoAccessToken;
        this.mercadopagoBaseUrl = mercadopagoBaseUrl;
        this.httpClient = httpClient;
        this.objectMapper = new ObjectMapper();
    }

    @PostMapping("/pix")
    public ResponseEntity<Void> handlePixNotification(
            @RequestHeader("X-Signature") String xSignature,
            @RequestHeader("X-Request-Id") String xRequestId,
            @RequestBody Map<String, Object> payload) {
        log.info("Notificação PIX recebida: X-Request-Id={}", xRequestId);

        try {
            String action = (String) payload.get("action");
            if (!"payment.updated".equals(action)) {
                log.info("Ação ignorada: {}", action);
                return ResponseEntity.ok().build();
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) payload.get("data");
            if (data == null || !data.containsKey("id")) {
                log.warn("Payload inválido: data.id ausente");
                return ResponseEntity.badRequest().build();
            }

            String paymentId = data.get("id").toString();

            if (!webhookValidator.validateSignature(xSignature, xRequestId, paymentId)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Busca status REAL do pagamento na API do Mercado Pago
            String realStatus = fetchPaymentStatusFromMercadoPago(paymentId);
            if (realStatus == null) {
                log.error("Não foi possível obter status real do pagamento {} na API do Mercado Pago", paymentId);
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
            }

            String mappedStatus = mapMercadoPagoStatus(realStatus);
            log.info("Status real do pagamento {}: MP={} mapeado={}", paymentId, realStatus, mappedStatus);

            if (mappedStatus != null) {
                notificationService.processarNotificacao(paymentId, mappedStatus);
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Erro ao processar notificação PIX", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Fetch real payment status from Mercado Pago API.
     * @return the MP status field, or null on failure
     */
    private String fetchPaymentStatusFromMercadoPago(String paymentId) {
        if (mercadopagoAccessToken == null || mercadopagoAccessToken.isBlank()) {
            log.error("Access token do Mercado Pago não configurado — impossível consultar status real do pagamento {}", paymentId);
            return null;
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(mercadopagoBaseUrl + "/v1/payments/" + paymentId))
                    .header("Authorization", "Bearer " + mercadopagoAccessToken)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("Resposta MP GET /v1/payments/{}: status={}", paymentId, response.statusCode());

            if (response.statusCode() == 404) {
                log.warn("Pagamento {} não encontrado no Mercado Pago", paymentId);
                return null;
            }

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                log.error("Erro ao consultar pagamento {} no MP: HTTP {}", paymentId, response.statusCode());
                return null;
            }

            JsonNode root = objectMapper.readTree(response.body());
            return root.path("status").asText(null);
        } catch (Exception e) {
            log.error("Erro ao consultar pagamento {} no Mercado Pago", paymentId, e);
            return null;
        }
    }

    /**
     * Map Mercado Pago status to internal status.
     * @return internal status string, or null if the status should be ignored (payment still pending)
     */
    private String mapMercadoPagoStatus(String mpStatus) {
        if (mpStatus == null) return null;
        return switch (mpStatus.toLowerCase()) {
            case "approved" -> "APROVADO";
            case "rejected", "cancelled" -> "REJEITADO";
            case "expired" -> "EXPIRADO";
            default -> {
                log.info("Status MP '{}' não requer ação (ainda pendente)", mpStatus);
                yield null;
            }
        };
    }
}