package com.supermarkettracker.infrastructure.integration.sumup;

import com.supermarkettracker.domain.exception.GatewayConfiguracaoException;
import com.supermarkettracker.domain.exception.GatewayIndisponivelException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;

/**
 * Obtém um access token OAuth de curta duração para o SDK Tap-to-Pay.
 *
 * <p>A API key usada para conciliação não é uma credencial de terminal e nunca
 * é devolvida ao aplicativo. O refresh token é guardado cifrado no servidor.</p>
 */
@Service
public class SumupTapToPayTokenService {
    private final RestClient sumupClient;
    private final String clientId;
    private final String clientSecret;
    private final String initialRefreshToken;
    private final SumupOAuthCredentialStore credentialStore;

    public SumupTapToPayTokenService(
            RestClient.Builder restClientBuilder,
            @Value("${SUMUP_OAUTH_CLIENT_ID:}") String clientId,
            @Value("${SUMUP_OAUTH_CLIENT_SECRET:}") String clientSecret,
            @Value("${SUMUP_OAUTH_REFRESH_TOKEN:}") String initialRefreshToken,
            SumupOAuthCredentialStore credentialStore) {
        this(restClientBuilder.baseUrl("https://api.sumup.com").build(), clientId, clientSecret,
                initialRefreshToken, credentialStore);
    }

    SumupTapToPayTokenService(
            RestClient sumupClient,
            String clientId,
            String clientSecret,
            String initialRefreshToken,
            SumupOAuthCredentialStore credentialStore) {
        this.sumupClient = sumupClient;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.initialRefreshToken = initialRefreshToken;
        this.credentialStore = credentialStore;
    }

    public AccessToken accessToken() {
        requireClientConfiguration();
        String refreshToken = credentialStore.refreshToken().orElse(initialRefreshToken);
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new GatewayConfiguracaoException(
                    "OAuth da SumUp ainda não foi autorizado. Inicie a autorização do merchant.");
        }

        var form = new LinkedMultiValueMap<String, String>();
        form.add("grant_type", "refresh_token");
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("refresh_token", refreshToken);

        Map<?, ?> response;
        try {
            response = sumupClient.post()
                    .uri("/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(Map.class);
        } catch (RestClientException exception) {
            throw new GatewayIndisponivelException(
                    "Não foi possível autenticar o terminal na SumUp. Tente novamente.", exception);
        }

        var token = response == null ? null : response.get("access_token");
        if (!(token instanceof String accessToken) || accessToken.isBlank()) {
            throw new GatewayIndisponivelException(
                    "A SumUp não retornou um access token válido para o terminal.");
        }
        var rotatedRefreshToken = response.get("refresh_token");
        if (rotatedRefreshToken instanceof String rotated && !rotated.isBlank()) {
            credentialStore.save(rotated);
        } else if (credentialStore.refreshToken().isEmpty()) {
            // Migra o valor inicial de ambiente para o armazenamento cifrado
            // após a primeira renovação bem-sucedida.
            credentialStore.save(refreshToken);
        }
        return new AccessToken(accessToken);
    }

    private void requireClientConfiguration() {
        if (clientId.isBlank() || clientSecret.isBlank()) {
            throw new GatewayConfiguracaoException(
                    "OAuth da SumUp não configurado. Defina SUMUP_OAUTH_CLIENT_ID e SUMUP_OAUTH_CLIENT_SECRET.");
        }
    }

    public record AccessToken(String accessToken) { }
}
