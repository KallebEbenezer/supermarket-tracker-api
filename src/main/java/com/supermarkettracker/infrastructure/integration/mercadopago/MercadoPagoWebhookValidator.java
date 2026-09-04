package com.supermarkettracker.infrastructure.integration.mercadopago;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.function.Supplier;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Valida a assinatura HMAC-SHA256 dos webhooks do Mercado Pago.
 *
 * <p>Formato do header {@code X-Signature}: pares chave=valor separados por vírgula,
 * contendo ao menos {@code ts} (timestamp em segundos) e {@code v1} (HMAC-SHA256 hex).
 *
 * <p>Manifesto assinado pelo MP (formato oficial, DOIS-PONTOS):
 * {@code "id:" + dataId + ";request-id:" + xRequestId + ";ts:" + ts + ";"}.
 * O formato legado com '=' também é aceito por compatibilidade; a validação
 * sempre exige o mesmo secret e o HMAC correspondente.
 */
@Component
public class MercadoPagoWebhookValidator {
    private static final Logger log = LoggerFactory.getLogger(MercadoPagoWebhookValidator.class);

    private static final long MAX_TIMESTAMP_SKEW_SECONDS = 300L; // 5 minutos — proteção contra replay
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final String SECRET_KEY_ALGORITHM = "HmacSHA256";

    private final String webhookSecret;

    @Autowired
    public MercadoPagoWebhookValidator(
            @Value("${app.mercadopago.webhook-secret:}") String webhookSecret) {
        this(webhookSecret, Instant::now);
    }

    // Package-private para testes determinísticos
    MercadoPagoWebhookValidator(String webhookSecret, Supplier<Instant> clock) {
        this.webhookSecret = webhookSecret;
        this.clock = clock;
    }

    private final Supplier<Instant> clock;

    public boolean validateSignature(String xSignature, String xRequestId, String dataId) {
        return validateSignature(xSignature, xRequestId, dataId, null);
    }

    public boolean validateSignature(String xSignature, String xRequestId, String dataId, String type) {
        if (xSignature == null || xSignature.isBlank()) {
            log.warn("Webhook rejeitado: X-Signature ausente");
            return false;
        }
        if (xRequestId == null || xRequestId.isBlank()) {
            log.warn("Webhook rejeitado: X-Request-Id ausente");
            return false;
        }
        if (dataId == null || dataId.isBlank()) {
            log.warn("Webhook rejeitado: data.id ausente");
            return false;
        }
        if (webhookSecret == null || webhookSecret.isBlank()) {
            log.error("Webhook rejeitado: app.mercadopago.webhook-secret não configurado");
            return false;
        }

        String ts = parsePair(xSignature, "ts");
        String v1 = parsePair(xSignature, "v1");
        if (ts == null || v1 == null) {
            log.warn("Webhook rejeitado: X-Signature malformado (esperado ts=...,v1=...)");
            return false;
        }

        long tsEpoch;
        try {
            tsEpoch = Long.parseLong(ts);
        } catch (NumberFormatException e) {
            log.warn("Webhook rejeitado: ts não é numérico");
            return false;
        }

        long now = clock.get().getEpochSecond();
        if (Math.abs(now - tsEpoch) > MAX_TIMESTAMP_SKEW_SECONDS) {
            log.warn("Webhook rejeitado: ts fora da janela de {}s (ts={}, now={})",
                    MAX_TIMESTAMP_SKEW_SECONDS, tsEpoch, now);
            return false;
        }

        byte[] received;
        try {
            received = HexFormat.of().parseHex(v1);
        } catch (IllegalArgumentException e) {
            log.warn("Webhook rejeitado: v1 não é hex válido");
            return false;
        }

        // O Mercado Pago assina o manifesto com DOIS-PONTOS dentro dos campos:
        //     id:<data.id>;request-id:<x-request-id>;ts:<ts>;
        // (v2, com type na frente em algumas contas). O formato antigo da casa
        // usava '=' — mantido como aceite para não regredir. Todos os formatos
        // exigem o MESMO secret, então HMAC inválido continua sendo rejeitado.
        for (String manifest : manifestosCandidatos(dataId, xRequestId, ts, type)) {
            byte[] expected;
            try {
                Mac mac = Mac.getInstance(HMAC_ALGORITHM);
                mac.init(new SecretKeySpec(
                        webhookSecret.getBytes(StandardCharsets.UTF_8), SECRET_KEY_ALGORITHM));
                expected = mac.doFinal(manifest.getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                log.error("Falha ao computar HMAC", e);
                return false;
            }
            if (MessageDigest.isEqual(expected, received)) {
                log.debug("Webhook validado: request-id={}, ts={}", xRequestId, tsEpoch);
                return true;
            }
        }

        log.warn("Webhook rejeitado: assinatura HMAC inválida");
        return false;
    }

    /** Formatos de manifesto aceitos para o HMAC (todos com o mesmo secret). */
    private static List<String> manifestosCandidatos(String dataId, String requestId, String ts, String type) {
        List<String> manifestos = new ArrayList<>();
        manifestos.add("id:" + dataId + ";request-id:" + requestId + ";ts:" + ts + ";"); // oficial MP
        manifestos.add("id=" + dataId + ";request-id=" + requestId + ";ts=" + ts + ";"); // legado (=)
        if (type != null && !type.isBlank()) {
            manifestos.add("id:" + dataId + ";type:" + type + ";request-id:" + requestId + ";ts:" + ts + ";");
            manifestos.add("id=" + dataId + ";type=" + type + ";request-id=" + requestId + ";ts=" + ts + ";");
        }
        return manifestos;
    }

    /** Extrai o valor de um par chave=valor do header X-Signature (separado por vírgula). */
    private static String parsePair(String xSignature, String key) {
        for (String part : xSignature.split(",")) {
            String trimmed = part.trim();
            String prefix = key + "=";
            if (trimmed.startsWith(prefix)) {
                return trimmed.substring(prefix.length());
            }
        }
        return null;
    }
}
