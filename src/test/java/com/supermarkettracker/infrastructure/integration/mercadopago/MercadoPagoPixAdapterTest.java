package com.supermarkettracker.infrastructure.integration.mercadopago;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.supermarkettracker.domain.exception.GatewayConfiguracaoException;
import com.supermarkettracker.domain.exception.GatewayIndisponivelException;
import com.supermarkettracker.domain.model.valueobject.Dinheiro;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;

class MercadoPagoPixAdapterTest {

    @Test
    void tokenAusenteComMockDesabilitado_throwsConfiguracao() {
        var adapter = new MercadoPagoPixAdapter("", "https://api.mercadopago.com", false, "payer@example.com");
        var id = Identificador.novo();
        assertThatThrownBy(() -> adapter.criarCobranca(id, new Dinheiro(new BigDecimal("10.00")), "chave"))
                .isInstanceOf(GatewayConfiguracaoException.class);
    }

    @Test
    void tokenAusenteComMockHabilitado_retornaCobrancaMock() {
        var adapter = new MercadoPagoPixAdapter("", "https://api.mercadopago.com", true, "payer@example.com");
        var id = Identificador.novo();
        var cobranca = adapter.criarCobranca(id, new Dinheiro(new BigDecimal("10.00")), "chave");

        assertThat(cobranca.gateway()).isEqualTo("mock");
        assertThat(cobranca.status()).isEqualTo("PENDENTE");
        assertThat(cobranca.copiaECola()).isNotBlank();
    }

    @Test
    void resposta500_throwsIndisponivel() throws Exception {
        var httpClient = mock(HttpClient.class);
        var httpResponse = mock(HttpResponse.class);
        when(httpResponse.statusCode()).thenReturn(503);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);

        var adapter = new MercadoPagoPixAdapter("token", "https://api.mercadopago.com", false, "payer@example.com", httpClient);
        var id = Identificador.novo();

        assertThatThrownBy(() -> adapter.criarCobranca(id, new Dinheiro(new BigDecimal("10.00")), "chave"))
                .isInstanceOf(GatewayIndisponivelException.class)
                .hasMessageContaining("503");
    }

    @Test
    void resposta400_throwsIndisponivel() throws Exception {
        var httpClient = mock(HttpClient.class);
        var httpResponse = mock(HttpResponse.class);
        when(httpResponse.statusCode()).thenReturn(400);
        when(httpResponse.body()).thenReturn("{\"message\":\"bad request\"}");
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);

        var adapter = new MercadoPagoPixAdapter("token", "https://api.mercadopago.com", false, "payer@example.com", httpClient);
        var id = Identificador.novo();

        assertThatThrownBy(() -> adapter.criarCobranca(id, new Dinheiro(new BigDecimal("10.00")), "chave"))
                .isInstanceOf(GatewayIndisponivelException.class);
    }

    @Test
    void resposta200SemQrCode_throwsIndisponivel() throws Exception {
        var httpClient = mock(HttpClient.class);
        var httpResponse = mock(HttpResponse.class);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn("{\"id\":\"pay_1\",\"status\":\"pending\"}");
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);

        var adapter = new MercadoPagoPixAdapter("token", "https://api.mercadopago.com", false, "payer@example.com", httpClient);
        var id = Identificador.novo();

        assertThatThrownBy(() -> adapter.criarCobranca(id, new Dinheiro(new BigDecimal("10.00")), "chave"))
                .isInstanceOf(GatewayIndisponivelException.class)
                .hasMessageContaining("qr_code");
    }

    @Test
    void respostaValida_retornaCobrancaMercadoPago() throws Exception {
        var httpClient = mock(HttpClient.class);
        var httpResponse = mock(HttpResponse.class);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(
                "{\"id\":\"pay_1\",\"status\":\"pending\",\"point_of_interaction\":{\"transaction_data\":{"
                        + "\"qr_code\":\"00020126...\",\"ticket_url\":\"https://mp.com/ticket\"}}}");
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);

        var adapter = new MercadoPagoPixAdapter("token", "https://api.mercadopago.com", false, "payer@example.com", httpClient);
        var id = Identificador.novo();

        var cobranca = adapter.criarCobranca(id, new Dinheiro(new BigDecimal("10.00")), "chave");
        assertThat(cobranca.gateway()).isEqualTo("mercadopago");
        assertThat(cobranca.transacaoId()).isEqualTo("pay_1");
        assertThat(cobranca.copiaECola()).isEqualTo("00020126...");
    }

    @Test
    void erroDeRede_throwsIndisponivel() throws Exception {
        var httpClient = mock(HttpClient.class);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new IOException("connection refused"));

        var adapter = new MercadoPagoPixAdapter("token", "https://api.mercadopago.com", false, "payer@example.com", httpClient);
        var id = Identificador.novo();

        assertThatThrownBy(() -> adapter.criarCobranca(id, new Dinheiro(new BigDecimal("10.00")), "chave"))
                .isInstanceOf(GatewayIndisponivelException.class)
                .hasMessageContaining("Falha de comunicação");
    }
}
