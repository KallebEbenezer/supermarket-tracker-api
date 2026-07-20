package com.supermarkettracker.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supermarkettracker.infrastructure.config.JwtProperties;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Service;

/**
 * Emite e valida tokens JWT (HS256) sem dependências externas, usando apenas
 * {@link Mac} da JDK e Jackson para (de)serializar os claims.
 *
 * <p>Claims: {@code sub} (email), {@code uid} (id do usuário), {@code roles},
 * {@code type} ("access" | "refresh"), {@code iat}, {@code exp}.
 */
@Service
public class TokenService {

    /** Resultado da validação de um token válido. */
    public record TokenPayload(UUID usuarioId, String email, String papel, String tipo) { }

    private static final String ACCESS = "access";
    private static final String REFRESH = "refresh";
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();

    private final JwtProperties properties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TokenService(JwtProperties properties) {
        this.properties = properties;
    }

    public String gerarAccessToken(UUID usuarioId, String email, String papel) {
        Instant agora = Instant.now();
        return gerar(usuarioId, email, papel, ACCESS,
                agora, agora.plus(properties.accessTtlMinutes(), ChronoUnit.MINUTES));
    }

    public String gerarRefreshToken(UUID usuarioId, String email, String papel) {
        Instant agora = Instant.now();
        return gerar(usuarioId, email, papel, REFRESH,
                agora, agora.plus(properties.refreshTtlDays(), ChronoUnit.DAYS));
    }

    public long accessTtlSegundos() {
        return properties.accessTtlMinutes() * 60;
    }

    /**
     * Valida assinatura e expiração e devolve os claims.
     *
     * @throws TokenInvalidoException se o token for malformado, adulterado ou expirado.
     */
    public TokenPayload validar(String token) {
        if (token == null || token.isBlank()) {
            throw new TokenInvalidoException("Token ausente");
        }
        String[] partes = token.split("\\.");
        if (partes.length != 3) {
            throw new TokenInvalidoException("Token malformado");
        }
        String conteudoAssinado = partes[0] + "." + partes[1];
        String assinaturaEsperada = assinar(conteudoAssinado);
        if (!constantTimeEquals(assinaturaEsperada, partes[2])) {
            throw new TokenInvalidoException("Assinatura inválida");
        }
        Map<String, Object> claims = lerClaims(partes[1]);
        Object exp = claims.get("exp");
        if (exp == null || Instant.now().getEpochSecond() > ((Number) exp).longValue()) {
            throw new TokenInvalidoException("Token expirado");
        }
        try {
            return new TokenPayload(
                    UUID.fromString((String) claims.get("uid")),
                    (String) claims.get("sub"),
                    (String) claims.get("roles"),
                    (String) claims.get("type"));
        } catch (RuntimeException e) {
            throw new TokenInvalidoException("Claims inválidos");
        }
    }

    public boolean ehRefresh(TokenPayload payload) {
        return REFRESH.equals(payload.tipo());
    }

    private String gerar(UUID usuarioId, String email, String papel, String tipo, Instant iat, Instant exp) {
        Map<String, Object> header = new LinkedHashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");

        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("sub", email);
        claims.put("uid", usuarioId.toString());
        claims.put("roles", papel);
        claims.put("type", tipo);
        claims.put("iat", iat.getEpochSecond());
        claims.put("exp", exp.getEpochSecond());

        String encabecado = ENCODER.encodeToString(escrever(header));
        String corpo = ENCODER.encodeToString(escrever(claims));
        String conteudo = encabecado + "." + corpo;
        return conteudo + "." + assinar(conteudo);
    }

    private byte[] escrever(Map<String, Object> valor) {
        try {
            return objectMapper.writeValueAsBytes(valor);
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao serializar JWT", e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> lerClaims(String payloadBase64) {
        try {
            byte[] json = DECODER.decode(payloadBase64);
            return objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            throw new TokenInvalidoException("Não foi possível ler o token");
        }
    }

    private String assinar(String conteudo) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(properties.secret().getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] assinatura = mac.doFinal(conteudo.getBytes(StandardCharsets.UTF_8));
            return ENCODER.encodeToString(assinatura);
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao assinar JWT", e);
        }
    }

    private boolean constantTimeEquals(String a, String b) {
        byte[] x = a.getBytes(StandardCharsets.UTF_8);
        byte[] y = b.getBytes(StandardCharsets.UTF_8);
        if (x.length != y.length) {
            return false;
        }
        int resultado = 0;
        for (int i = 0; i < x.length; i++) {
            resultado |= x[i] ^ y[i];
        }
        return resultado == 0;
    }
}
