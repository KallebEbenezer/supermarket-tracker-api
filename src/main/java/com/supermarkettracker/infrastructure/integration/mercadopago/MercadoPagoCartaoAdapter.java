package com.supermarkettracker.infrastructure.integration.mercadopago;

import com.supermarkettracker.domain.gateway.CartaoGateway;
import com.supermarkettracker.domain.model.enums.ModalidadeCartao;
import com.supermarkettracker.domain.model.valueobject.Dinheiro;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.cartao.provider", havingValue = "mercadopago", matchIfMissing = true)
public class MercadoPagoCartaoAdapter implements CartaoGateway {
    private static final Logger log = LoggerFactory.getLogger(MercadoPagoCartaoAdapter.class);

    private final String accessToken;
    private final String baseUrl;
    private final HttpClient httpClient;

    public MercadoPagoCartaoAdapter(
            @Value("${app.mercadopago.access-token:}") String accessToken,
            @Value("${app.mercadopago.base-url:https://api.mercadopago.com}") String baseUrl) {
        this.accessToken = accessToken;
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newHttpClient();
    }

    @Override
    public TransacaoCartao processar(Identificador pagamentoId, Dinheiro valor,
                                     ModalidadeCartao modalidade, short parcelas, String tokenCartao) {
        if (accessToken == null || accessToken.isBlank()) {
            log.warn("Access token do Mercado Pago não configurado para cartão");
            throw new RuntimeException("Gateway de cartão não configurado: token Mercado Pago ausente");
        }

        // ⚠️ ATENÇÃO: o tokenCartao chega como dado bruto do client. Em produção,
        // este adapter deveria receber SOMENTE tokens já tokenizados por SDK do
        // Mercado Pago executado no device (PCI-DSS: nunca trafegar dados de cartão).
        // Manter validação de formato como salvaguarda.
        if (tokenCartao == null || tokenCartao.isBlank()) {
            throw new RuntimeException("tokenCartao obrigatório para processar pagamento com cartão");
        }
        if (tokenCartao.length() < 32 || tokenCartao.length() > 64) {
            log.error("tokenCartao com formato suspeito (len={}). Esperado token do SDK MP (32-64 chars). "
                    + "Se este é um PAN real, há vazamento de PCI-DSS — investigar imediatamente.",
                    tokenCartao.length());
            throw new RuntimeException("tokenCartao com formato inválido");
        }

        String body = buildPaymentBody(valor.valor(), modalidade, parcelas, tokenCartao);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/v1/payments"))
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("Resposta Mercado Pago cartão: status={}, body={}", response.statusCode(), response.body());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return parseResponse(response.body(), pagamentoId);
            } else {
                String msg = extractJsonString(response.body(), "message");
                throw new RuntimeException("Erro ao processar cartão: " +
                        (msg != null ? msg : "HTTP " + response.statusCode()));
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao comunicar com Mercado Pago (cartão)", e);
            throw new RuntimeException("Falha na comunicação com gateway de pagamento: " + e.getMessage(), e);
        }
    }

    private String buildPaymentBody(BigDecimal valor, ModalidadeCartao modalidade,
                                     short parcelas, String tokenCartao) {
        String paymentMethod = modalidade == ModalidadeCartao.DEBITO ? "debit_card" : "credit_card";

        return """
                {
                    "transaction_amount": %s,
                    "token": "%s",
                    "description": "Pagamento Supermarket Tracker",
                    "payment_method_id": "%s",
                    "installments": %d,
                    "payer": {
                        "type": "guest"
                    }
                }
                """.formatted(
                valor.toPlainString(),
                tokenCartao,
                paymentMethod,
                parcelas
        );
    }

    private TransacaoCartao parseResponse(String responseBody, Identificador pagamentoId) {
        String status = extractJsonString(responseBody, "status");
        String id = extractJsonString(responseBody, "id");
        String authorizationCode = extractJsonString(responseBody, "authorization_code");
        String statusDetail = extractJsonString(responseBody, "status_detail");

        String mappedStatus;
        if ("approved".equalsIgnoreCase(status)) {
            mappedStatus = "APROVADO";
        } else if ("rejected".equalsIgnoreCase(status) || "cancelled".equalsIgnoreCase(status)) {
            mappedStatus = "RECUSADO";
        } else {
            mappedStatus = "PENDENTE";
        }

        String codigoAutorizacao = authorizationCode != null ? authorizationCode : id;

        log.info("Pagamento cartão processado: id={}, status={}, detail={}", id, mappedStatus, statusDetail);

        return new TransacaoCartao("mercadopago", id, mappedStatus, codigoAutorizacao);
    }

    private String extractJsonString(String json, String key) {
        String search = "\"" + key + "\":";
        int start = json.indexOf(search);
        if (start == -1) return null;
        start += search.length();
        while (start < json.length() && json.charAt(start) == ' ') start++;
        if (start >= json.length()) return null;

        if (json.charAt(start) == '"') {
            start++;
            int end = json.indexOf("\"", start);
            if (end == -1) return null;
            return json.substring(start, end);
        }
        int end = json.indexOf(",", start);
        if (end == -1) end = json.indexOf("}", start);
        if (end == -1) return null;
        return json.substring(start, end).trim();
    }
}
