package com.supermarkettracker.infrastructure.integration.sumup;

import com.supermarkettracker.domain.exception.GatewayConfiguracaoException;
import com.supermarkettracker.domain.exception.GatewayIndisponivelException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

/** Executa o Authorization Code Flow sem devolver códigos ou tokens ao navegador. */
@Service
public class SumupOAuthAuthorizationService {
    private static final String AUTHORIZE_URL = "https://api.sumup.com/authorize";
    private final RestClient sumupClient;
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private final SumupOAuthStateStore stateStore;
    private final SumupOAuthCredentialStore credentialStore;
    private final SecureRandom random = new SecureRandom();

    public SumupOAuthAuthorizationService(
            RestClient.Builder restClientBuilder,
            @Value("${SUMUP_OAUTH_CLIENT_ID:}") String clientId,
            @Value("${SUMUP_OAUTH_CLIENT_SECRET:}") String clientSecret,
            @Value("${SUMUP_OAUTH_REDIRECT_URI:}") String redirectUri,
            SumupOAuthStateStore stateStore,
            SumupOAuthCredentialStore credentialStore) {
        this(restClientBuilder.baseUrl("https://api.sumup.com").build(), clientId, clientSecret, redirectUri,
                stateStore, credentialStore);
    }

    SumupOAuthAuthorizationService(RestClient sumupClient, String clientId, String clientSecret, String redirectUri,
                                   SumupOAuthStateStore stateStore, SumupOAuthCredentialStore credentialStore) {
        this.sumupClient = sumupClient;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.stateStore = stateStore;
        this.credentialStore = credentialStore;
    }

    public AuthorizationUrl startAuthorization() {
        requireConfiguration();
        String state = randomState();
        stateStore.save(state);
        String url = UriComponentsBuilder.fromUriString(AUTHORIZE_URL)
                .queryParam("response_type", "code")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("state", state)
                .build().encode().toUriString();
        return new AuthorizationUrl(url);
    }

    public void finishAuthorization(String code, String state) {
        requireConfiguration();
        if (code == null || code.isBlank() || state == null || state.isBlank() || !stateStore.consume(state)) {
            throw new IllegalArgumentException("Retorno OAuth inválido ou expirado.");
        }

        var form = new LinkedMultiValueMap<String, String>();
        form.add("grant_type", "authorization_code");
        form.add("code", code);
        form.add("redirect_uri", redirectUri);
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        try {
            Map<?, ?> response = sumupClient.post().uri("/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED).body(form).retrieve().body(Map.class);
            Object refreshToken = response == null ? null : response.get("refresh_token");
            if (!(refreshToken instanceof String refresh) || refresh.isBlank()) {
                throw new GatewayIndisponivelException("A SumUp não retornou um refresh token válido.");
            }
            credentialStore.save(refresh);
        } catch (RestClientException exception) {
            throw new GatewayIndisponivelException("Não foi possível concluir a autorização na SumUp.", exception);
        }
    }

    private void requireConfiguration() {
        if (clientId.isBlank() || clientSecret.isBlank() || redirectUri.isBlank()) {
            throw new GatewayConfiguracaoException("OAuth da SumUp não configurado. Defina o client, secret e redirect URI.");
        }
    }

    private String randomState() {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public record AuthorizationUrl(String authorizationUrl) { }
}
