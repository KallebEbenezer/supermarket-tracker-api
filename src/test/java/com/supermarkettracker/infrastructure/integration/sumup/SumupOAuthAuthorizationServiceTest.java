package com.supermarkettracker.infrastructure.integration.sumup;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class SumupOAuthAuthorizationServiceTest {
    @Test
    void troca_codigo_por_refresh_token_sem_devolve_lo_ao_navegador() {
        var builder = RestClient.builder().baseUrl("https://api.sumup.com");
        var server = MockRestServiceServer.bindTo(builder).build();
        var stateStore = mock(SumupOAuthStateStore.class);
        var credentialStore = mock(SumupOAuthCredentialStore.class);
        when(stateStore.consume("valid-state")).thenReturn(true);
        var service = new SumupOAuthAuthorizationService(builder.build(), "client-id", "client-secret",
                "https://example.com/callback", stateStore, credentialStore);

        server.expect(requestTo("https://api.sumup.com/token"))
                .andExpect(method(POST))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(content().string(org.hamcrest.Matchers.allOf(
                        org.hamcrest.Matchers.containsString("grant_type=authorization_code"),
                        org.hamcrest.Matchers.containsString("redirect_uri=https%3A%2F%2Fexample.com%2Fcallback"))))
                .andRespond(withSuccess("{\"access_token\":\"short-lived\",\"refresh_token\":\"refresh-secret\"}",
                        MediaType.APPLICATION_JSON));

        service.finishAuthorization("single-use-code", "valid-state");

        verify(credentialStore).save("refresh-secret");
        server.verify();
    }
}
