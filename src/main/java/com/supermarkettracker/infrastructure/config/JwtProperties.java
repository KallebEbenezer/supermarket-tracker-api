package com.supermarkettracker.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Propriedades de configuração dos tokens JWT.
 *
 * <p>Valores default são adequados para desenvolvimento. Em produção,
 * {@code app.jwt.secret} deve ser sobrescrito por variável de ambiente
 * ({@code APP_JWT_SECRET}) com pelo menos 32 caracteres (256 bits).
 */
@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(String secret, long accessTtlMinutes, long refreshTtlDays) {
    public JwtProperties {
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException(
                    "app.jwt.secret deve ter ao menos 32 caracteres (256 bits) para HS256");
        }
        if (accessTtlMinutes <= 0) {
            accessTtlMinutes = 15;
        }
        if (refreshTtlDays <= 0) {
            refreshTtlDays = 7;
        }
    }
}
