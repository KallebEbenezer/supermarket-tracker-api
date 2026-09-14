package com.supermarkettracker.infrastructure.integration.sumup;

import com.supermarkettracker.domain.exception.GatewayConfiguracaoException;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.http.HttpMethod.POST;

class SumupTapToPayTokenServiceTest {

    @Test
    void troca_refresh_token_por_access_token_sem_expor_api_key() {
        var builder = RestClient.builder().baseUrl("https://api.sumup.com");
        var server = MockRestServiceServer.bindTo(builder).build();
        var service = new SumupTapToPayTokenService(
                builder.build(), "client-id", "client-secret", "refresh-token");

        server.expect(requestTo("https://api.sumup.com/token"))
                .andExpect(method(POST))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(content().string(org.hamcrest.Matchers.allOf(
                        org.hamcrest.Matchers.containsString("grant_type=refresh_token"),
                        org.hamcrest.Matchers.containsString("client_id=client-id"),
                        org.hamcrest.Matchers.containsString("client_secret=client-secret"),
                        org.hamcrest.Matchers.containsString("refresh_token=refresh-token"))))
                .andRespond(withSuccess("{\"access_token\":\"short-lived-token\"}", MediaType.APPLICATION_JSON));

        assertThat(service.accessToken().accessToken()).isEqualTo("short-lived-token");
        server.verify();
    }

    @Test
    void rejeita_configuracao_oauth_incompleta_antes_de_chamar_a_sumup() {
        var service = new SumupTapToPayTokenService(
                RestClient.create(), "client-id", "", "refresh-token");

        assertThatThrownBy(service::accessToken)
                .isInstanceOf(GatewayConfiguracaoException.class)
                .hasMessageContaining("SUMUP_OAUTH_CLIENT_SECRET");
    }
}
