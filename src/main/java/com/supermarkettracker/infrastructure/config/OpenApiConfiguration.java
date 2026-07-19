package com.supermarkettracker.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {
    @Bean
    OpenAPI supermarketTrackerOpenApi() {
        return new OpenAPI().info(new Info().title("Supermarket Tracker API")
                .version("v1").description("API REST para cadastro comercial, caixa, estoque, vendas e dashboard."))
                .tags(List.of(
                        new Tag().name("Empresas").description("Cadastro de empresas."),
                        new Tag().name("Lojas").description("Cadastro e consulta de lojas."),
                        new Tag().name("Produtos").description("Catálogo de produtos."),
                        new Tag().name("Clientes e usuários").description("Cadastros de pessoas e operadores."),
                        new Tag().name("Caixa").description("Caixas e sessões de operação."),
                        new Tag().name("Estoque").description("Movimentações de estoque."),
                        new Tag().name("Vendas").description("Finalização e consulta de vendas."),
                        new Tag().name("Dashboard").description("Indicadores comerciais consolidados.")));
    }
}
