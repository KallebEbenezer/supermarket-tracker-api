package com.supermarkettracker.infrastructure.integration.mercadopago;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.supermarkettracker.domain.exception.GatewayConfiguracaoException;
import com.supermarkettracker.domain.exception.GatewayIndisponivelException;
import com.supermarkettracker.domain.gateway.PixGateway;
import com.supermarkettracker.domain.model.valueobject.Dinheiro;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.infrastructure.integration.pix.PixPayloadBuilder;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MercadoPagoPixAdapter implements PixGateway {

    private static final Logger log = LoggerFactory.getLogger(MercadoPagoPixAdapter.class);

    private final String accessToken;
    private final String baseUrl;
    private final boolean mockEnabled;
    private final String payerEmail;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public MercadoPagoPixAdapter(
            @Value("${app.pix.mercadopago.access-token:}") String accessToken,
            @Value("${app.pix.mercadopago.base-url:https://api.mercadopago.com}") String baseUrl,
            @Value("${app.pix.mock-enabled:true}") boolean mockEnabled,
            @Value("${app.pix.mercadopago.payer-email:}") String payerEmail) {
        this(accessToken, baseUrl, mockEnabled, payerEmail, HttpClient.newHttpClient());
    }

    // Package-private for testing
    MercadoPagoPixAdapter(String accessToken, String baseUrl, boolean mockEnabled,
                          String payerEmail, HttpClient httpClient) {
        this.accessToken = accessToken;
        this.baseUrl = baseUrl;
        this.mockEnabled = mockEnabled;
        // O Mercado Pago exige um `payer.email` válido ao criar cobrança PIX.
        this.payerEmail = (payerEmail == null || payerEmail.isBlank())
                ? "pix@supermarkettracker.com.br"
                : payerEmail;
        this.httpClient = httpClient;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public CobrancaPix criarCobranca(Identificador pagamentoId, Dinheiro valor, String pixKey) {
        boolean tokenAusente = accessToken == null || accessToken.isBlank();

        if (tokenAusente) {
            if (mockEnabled) {
                log.warn("Access token do Mercado Pago ausente e mock habilitado — gerando PIX local (dev)");
                return gerarPixLocal(pagamentoId, valor, pixKey);
            }
            throw new GatewayConfiguracaoException(
                    "Token do Mercado Pago não configurado para PIX (defina PIX_MERCADOPAGO_ACCESS_TOKEN)");
        }

        String txid = pagamentoId.valor().toString().replace("-", "")
                .substring(0, Math.min(26, pagamentoId.valor().toString().replace("-", "").length()));
        String body = buildPixPaymentBody(valor.valor(), txid);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/v1/payments"))
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content-Type", "application/json")
                    .header("X-Idempotency-Key", txid)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("Resposta Mercado Pago: status={}", response.statusCode());

            if (response.statusCode() >= 500) {
                throw new GatewayIndisponivelException(
                        "Mercado Pago retornou HTTP " + response.statusCode() + " ao criar cobrança PIX");
            }
            if (response.statusCode() >= 400) {
                log.error("Mercado Pago rejeitou criação de PIX: status={}, body={}",
                        response.statusCode(), response.body());
                throw new GatewayIndisponivelException(
                        "Mercado Pago rejeitou a cobrança PIX (HTTP " + response.statusCode() + ")");
            }

            return parseRespostaMercadoPago(response.body(), pagamentoId, valor, pixKey);

        } catch (GatewayIndisponivelException | GatewayConfiguracaoException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro de comunicação com Mercado Pago ao criar PIX", e);
            throw new GatewayIndisponivelException("Falha de comunicação com Mercado Pago: " + e.getMessage(), e);
        }
    }

    private CobrancaPix gerarPixLocal(Identificador pagamentoId, Dinheiro valor, String pixKey) {
        String txid = pagamentoId.valor().toString().replace("-", "");
        if (txid.length() > 25) txid = txid.substring(0, 25);

        String copiaECola = PixPayloadBuilder.build(
                pixKey,
                "SUPERMARKET TRACKER",
                "SAO PAULO",
                valor.valor(),
                txid
        );

        Instant expiracao = Instant.now().plus(30, ChronoUnit.MINUTES);
        return new CobrancaPix("mock", txid, "PENDENTE", copiaECola, copiaECola, expiracao.toString());
    }

    private String buildPixPaymentBody(BigDecimal valor, String txid) {
        Instant expiracao = Instant.now().plus(30, ChronoUnit.MINUTES);
        // MP exige date_of_expiration no formato yyyy-MM-dd'T'HH:mm:ss.SSSXXX
        // (com milissegundos e offset de fuso), ex.: 2026-09-04T03:00:00.000-03:00.
        String expiracaoFormatada = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
                .withZone(ZoneId.systemDefault())
                .format(expiracao);
        return """
                {
                    "transaction_amount": %s,
                    "description": "Pagamento Supermarket Tracker",
                    "payment_method_id": "pix",
                    "payer": { "email": "%s" },
                    "external_reference": "%s",
                    "date_of_expiration": "%s"
                }
                """.formatted(
                valor.toPlainString(),
                payerEmail,
                txid,
                expiracaoFormatada
        );
    }

    private CobrancaPix parseRespostaMercadoPago(
            String responseBody,
            Identificador pagamentoId,
            Dinheiro valor,
            String pixKey) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);

            String id = root.path("id").asText(null);
            String status = root.path("status").asText("PENDENTE");

            // point_of_interaction → transaction_data → qr_code / qr_code_base64 / ticket_url
            JsonNode transactionData = root.path("point_of_interaction").path("transaction_data");
            String qrCode = transactionData.path("qr_code").asText(null);
            String ticketUrl = transactionData.path("ticket_url").asText(null);
            String qrCodeBase64 = transactionData.path("qr_code_base64").asText(null);

            if (qrCode == null && ticketUrl == null) {
                throw new GatewayIndisponivelException(
                        "Mercado Pago não retornou qr_code nem ticket_url na resposta");
            }

            String copiaECola = qrCode != null ? qrCode : ticketUrl;
            String imagemQr = qrCodeBase64 != null ? qrCodeBase64 : copiaECola;

            Instant expiracao = Instant.now().plus(30, ChronoUnit.MINUTES);
            return new CobrancaPix("mercadopago", id, status, imagemQr, copiaECola, expiracao.toString());

        } catch (GatewayIndisponivelException e) {
            throw e;
        } catch (Exception e) {
            throw new GatewayIndisponivelException(
                    "Erro ao parsear resposta do Mercado Pago: " + e.getMessage(), e);
        }
    }
}
