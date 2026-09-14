package com.supermarkettracker.infrastructure.integration.sumup;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.supermarkettracker.domain.gateway.CartaoGateway;
import com.supermarkettracker.domain.model.enums.ModalidadeCartao;
import com.supermarkettracker.domain.model.valueobject.Dinheiro;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Concilia no backend um pagamento Tap-to-Pay já aprovado no device.
 *
 * O app envia:
 *   - tokenCartao = txCode
 *   - referencia  = serverTransactionId
 *
 * Este adapter consulta a API da SumUp e só persiste APROVADO quando o status
 * remoto for de sucesso. Sem SUMUP_API_KEY o checkout falha (não há mock).
 */
@Component
@ConditionalOnProperty(name = "app.cartao.provider", havingValue = "sumup")
public class SumupCartaoAdapter implements CartaoGateway {
    private static final Logger log = LoggerFactory.getLogger(SumupCartaoAdapter.class);
    private static final String TRANSACTIONS =
            "https://api.sumup.com/v2.1/merchants/%s/transactions";

    @Value("${SUMUP_API_KEY:${SUMUP_SECRET_KEY:${app.sumup.secret-key:}}}")
    private String apiKey;

    @Value("${SUMUP_MERCHANT_CODE:${app.sumup.merchant-code:}}")
    private String merchantCode;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public TransacaoCartao processar(Identificador pagamentoId, Dinheiro valor,
                                     ModalidadeCartao modalidade, short parcelas, String tokenCartao) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new RuntimeException("SUMUP_API_KEY não configurada");
        }
        if (merchantCode == null || merchantCode.isBlank()) {
            throw new RuntimeException("SUMUP_MERCHANT_CODE não configurado");
        }
        if (tokenCartao == null || tokenCartao.isBlank()) {
            throw new RuntimeException("ID da transação SumUp obrigatório");
        }

        List<String> candidatos = idsCandidatos(tokenCartao);
        Exception lastError = null;

        for (String id : candidatos) {
            try {
                JsonNode root = consultarTransacao(id);
                if (root == null) {
                    continue;
                }
                return mapear(root, id, valor.valor());
            } catch (RuntimeException e) {
                lastError = e;
                log.warn("Consulta SumUp falhou para idPrefix={} msg={}", tokenPreview(id), e.getMessage());
            } catch (Exception e) {
                lastError = e;
                log.warn("Falha na comunicação SumUp: idPrefix={} msg={}", tokenPreview(id), e.getMessage());
            }
        }

        if (lastError instanceof RuntimeException runtime) {
            throw runtime;
        }
        throw new RuntimeException("Falha na consulta SumUp", lastError);
    }

    private List<String> idsCandidatos(String tokenCartao) {
        LinkedHashSet<String> ids = new LinkedHashSet<>();
        for (String parte : tokenCartao.split("[|,;\\s]+")) {
            if (parte != null && !parte.isBlank()) {
                ids.add(parte.trim());
            }
        }
        return new ArrayList<>(ids);
    }

    private JsonNode consultarTransacao(String id) throws Exception {
        // txCode e serverTransactionId têm formatos distintos, mas a API aceita
        // ambos. Tentamos os dois selectores no endpoint oficial e sempre dentro
        // do merchant configurado no servidor.
        for (String parametro : List.of("transaction_code", "id")) {
            HttpResponse<String> response = get(parametro, id);
            if (response.statusCode() == 200) {
                return primeiroItem(objectMapper.readTree(response.body()));
            }
            if (response.statusCode() != 404 && response.statusCode() != 400) {
                log.warn("Erro ao consultar SumUp por {}: http={} idPrefix={}",
                        parametro, response.statusCode(), tokenPreview(id));
                throw new RuntimeException("Erro na consulta SumUp: " + response.statusCode());
            }
        }
        return null;
    }

    private HttpResponse<String> get(String parameter, String value) throws Exception {
        String encodedMerchant = URLEncoder.encode(merchantCode, StandardCharsets.UTF_8);
        String encodedValue = URLEncoder.encode(value, StandardCharsets.UTF_8);
        String url = TRANSACTIONS.formatted(encodedMerchant) + "?" + parameter + "=" + encodedValue;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(15))
                .header("Authorization", "Bearer " + apiKey)
                .header("Accept", "application/json")
                .GET()
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private TransacaoCartao mapear(JsonNode root, String fallbackId, BigDecimal valorEsperado) {
        String rawStatus = findFirstText(root, Set.of("status", "transaction_status", "state"));
        if (rawStatus == null || rawStatus.isBlank()) {
            throw new RuntimeException("Resposta SumUp sem status de transação");
        }

        String mappedStatus = mapStatus(rawStatus);
        validarMerchant(root);
        validarValor(root, valorEsperado);
        String idTransacao = findFirstText(root, Set.of(
                "transaction_code", "transactionCode", "transaction_id", "id", "server_transaction_id"));
        String authCode = findFirstText(root, Set.of(
                "authorization_code", "authorizationCode", "auth_code", "authCode"));

        return new TransacaoCartao(
                "sumup",
                idTransacao != null && !idTransacao.isBlank() ? idTransacao : fallbackId,
                mappedStatus,
                authCode
        );
    }

    private void validarMerchant(JsonNode root) {
        String merchantDaTransacao = findFirstText(root, Set.of("merchant_code", "merchantCode"));
        if (merchantDaTransacao != null && !merchantCode.equalsIgnoreCase(merchantDaTransacao)) {
            throw new RuntimeException("Transação SumUp pertence a outro merchant");
        }
    }

    private void validarValor(JsonNode root, BigDecimal valorEsperado) {
        JsonNode amount = root.get("amount");
        if (amount == null || !amount.isNumber()) {
            throw new RuntimeException("Resposta SumUp sem valor da transação");
        }
        BigDecimal valorSumup = amount.decimalValue().setScale(2, RoundingMode.HALF_UP);
        if (valorSumup.compareTo(valorEsperado) != 0) {
            throw new RuntimeException("Valor da transação SumUp diverge do pagamento");
        }
        String currency = findFirstText(root, Set.of("currency"));
        if (currency != null && !"BRL".equalsIgnoreCase(currency)) {
            throw new RuntimeException("Moeda da transação SumUp inválida: " + currency);
        }
    }

    private String mapStatus(String rawStatus) {
        String s = rawStatus.trim().toUpperCase();
        if (s.contains("SUCCESS") || s.contains("APPROV") || s.contains("COMPLETED")
                || s.contains("PAID") || s.equals("SUCCESSFUL")) {
            return "APROVADO";
        }
        if (s.contains("FAIL") || s.contains("DECLIN") || s.contains("CANCEL")
                || s.contains("REJECT") || s.contains("ERROR")) {
            return "RECUSADO";
        }
        throw new RuntimeException("Status SumUp desconhecido: " + rawStatus);
    }

    private JsonNode primeiroItem(JsonNode node) {
        if (node == null) {
            return null;
        }
        if (node.isArray() && node.size() > 0) {
            return node.get(0);
        }
        if (node.isObject()) {
            for (String key : List.of("items", "data", "transactions", "results")) {
                JsonNode nested = node.get(key);
                if (nested != null && nested.isArray() && nested.size() > 0) {
                    return nested.get(0);
                }
            }
        }
        return node;
    }

    private String findFirstText(JsonNode node, Set<String> keys) {
        if (node == null) {
            return null;
        }
        if (node.isObject()) {
            var it = node.fields();
            while (it.hasNext()) {
                var entry = it.next();
                JsonNode value = entry.getValue();
                if (keys.contains(entry.getKey()) && value != null && !value.isNull()) {
                    if (value.isTextual() || value.isNumber()) {
                        String text = value.asText();
                        if (text != null && !text.isBlank()) {
                            return text;
                        }
                    }
                }
                String nested = findFirstText(value, keys);
                if (nested != null) {
                    return nested;
                }
            }
        } else if (node.isArray()) {
            for (JsonNode child : node) {
                String nested = findFirstText(child, keys);
                if (nested != null) {
                    return nested;
                }
            }
        }
        return null;
    }

    private String tokenPreview(String token) {
        if (token == null) {
            return "";
        }
        return token.substring(0, Math.min(8, token.length()));
    }
}
