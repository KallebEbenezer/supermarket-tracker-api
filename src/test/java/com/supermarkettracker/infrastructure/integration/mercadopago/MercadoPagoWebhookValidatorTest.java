package com.supermarkettracker.infrastructure.integration.mercadopago;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;

class MercadoPagoWebhookValidatorTest {

    private static final String SECRET = "test-secret";
    private static final String DATA_ID = "12345";
    private static final String REQUEST_ID = "req-abc";

    @Test
    void rejeitaQuandoXSignatureAusente() {
        var validator = new MercadoPagoWebhookValidator(SECRET, Instant::now);
        assertThat(validator.validateSignature(null, REQUEST_ID, DATA_ID)).isFalse();
    }

    @Test
    void rejeitaQuandoXRequestIdAusente() {
        var validator = new MercadoPagoWebhookValidator(SECRET, Instant::now);
        assertThat(validator.validateSignature("ts=1,v1=abc", null, DATA_ID)).isFalse();
    }

    @Test
    void rejeitaQuandoDataIdAusente() {
        var validator = new MercadoPagoWebhookValidator(SECRET, Instant::now);
        assertThat(validator.validateSignature("ts=1,v1=abc", REQUEST_ID, null)).isFalse();
    }

    @Test
    void rejeitaQuandoSecretAusente() {
        var validator = new MercadoPagoWebhookValidator("", Instant::now);
        assertThat(validator.validateSignature("ts=1,v1=abc", REQUEST_ID, DATA_ID)).isFalse();
    }

    @Test
    void rejeitaQuandoXSignatureMalformado() {
        var validator = new MercadoPagoWebhookValidator(SECRET, Instant::now);
        assertThat(validator.validateSignature("not-valid-format", REQUEST_ID, DATA_ID)).isFalse();
    }

    @Test
    void rejeitaQuandoTimestampForaDaJanela() {
        long tsMuitoAntigo = Instant.now().getEpochSecond() - 3600L;
        String v1 = computeV1(tsMuitoAntigo);
        var validator = new MercadoPagoWebhookValidator(SECRET, Instant::now);
        assertThat(validator.validateSignature("ts=" + tsMuitoAntigo + ",v1=" + v1, REQUEST_ID, DATA_ID)).isFalse();
    }

    @Test
    void rejeitaQuandoV1NaoHex() {
        long ts = Instant.now().getEpochSecond();
        var validator = new MercadoPagoWebhookValidator(SECRET, Instant::now);
        assertThat(validator.validateSignature("ts=" + ts + ",v1=zzz", REQUEST_ID, DATA_ID)).isFalse();
    }

    @Test
    void rejeitaQuandoV1Diferente() {
        long ts = Instant.now().getEpochSecond();
        var validator = new MercadoPagoWebhookValidator(SECRET, Instant::now);
        assertThat(validator.validateSignature("ts=" + ts + ",v1=deadbeef", REQUEST_ID, DATA_ID)).isFalse();
    }

    @Test
    void aceitaAssinaturaValida() {
        long ts = Instant.now().getEpochSecond();
        String v1 = computeV1(ts);
        var validator = new MercadoPagoWebhookValidator(SECRET, Instant::now);
        assertThat(validator.validateSignature("ts=" + ts + ",v1=" + v1, REQUEST_ID, DATA_ID)).isTrue();
    }

    @Test
    void rejeitaQuandoDataIdFoiAlterado() {
        long ts = Instant.now().getEpochSecond();
        String v1 = computeV1(ts);
        var validator = new MercadoPagoWebhookValidator(SECRET, Instant::now);
        assertThat(validator.validateSignature("ts=" + ts + ",v1=" + v1, REQUEST_ID, "9999")).isFalse();
    }

    private static String computeV1(long ts) {
        try {
            String manifest = "id=" + DATA_ID + ";request-id=" + REQUEST_ID + ";ts=" + ts + ";";
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] digest = mac.doFinal(manifest.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
