package com.supermarkettracker.infrastructure.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.supermarkettracker.application.dto.ProdutoDto;
import com.supermarkettracker.application.usecase.BuscarProdutoUseCase;
import com.supermarkettracker.application.usecase.CadastrarProdutoUseCase;
import com.supermarkettracker.infrastructure.web.controller.ProdutoController;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProdutoController.class)
@Import(SecurityConfiguration.class)
class SecurityConfigurationTest {

    private static final String PRODUTO_REQUEST = """
            {
              "empresaId": "de4c5433-767a-47d0-8e01-bf4dc722133a",
              "codigoBarras": "7891000100103",
              "nome": "Arroz Tipo 1 - 5kg",
              "unidadeMedida": "UN",
              "precoCompra": 20.00,
              "precoVenda": 28.50,
              "estoqueMinimo": 5.000,
              "permiteEstoqueNegativo": false
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CadastrarProdutoUseCase cadastrarProduto;

    @MockBean
    private BuscarProdutoUseCase buscarProduto;

    @Test
    void postProdutoAutenticadoNaoExigeTokenCsrf() throws Exception {
        when(cadastrarProduto.executar(any())).thenReturn(new ProdutoDto(UUID.randomUUID(),
                UUID.fromString("de4c5433-767a-47d0-8e01-bf4dc722133a"), "7891000100103",
                "Arroz Tipo 1 - 5kg", new BigDecimal("28.50"), BigDecimal.ZERO, "ATIVO"));

        mockMvc.perform(post("/api/v1/produtos")
                        .with(user("user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PRODUTO_REQUEST))
                .andExpect(status().isCreated());
    }

    @Test
    void postProdutoSemAutenticacaoERecusado() throws Exception {
        mockMvc.perform(post("/api/v1/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PRODUTO_REQUEST))
                .andExpect(status().isUnauthorized());
    }
}
