# Supermarket Tracker API

Backend REST para operação comercial de supermercados: cadastros, caixas, estoque, vendas, pagamentos e indicadores. O projeto usa Java 21, Spring Boot, PostgreSQL e Flyway, organizado em camadas de domínio, aplicação e infraestrutura.

## Comece aqui

| Necessário | Versão/uso |
| --- | --- |
| Java | 21 |
| Maven | 3.9 ou superior |
| PostgreSQL | 17 para desenvolvimento local |
| Docker Compose | alternativa recomendada para subir aplicação e banco |

```bash
# 1. Suba somente o banco para desenvolvimento local
docker compose up -d postgres

# 2. Inicie a API usando o perfil dev (padrão)
mvn spring-boot:run
```

A API fica disponível em `http://localhost:8080`. O Flyway aplica as migrations automaticamente. Consulte [instruções completas de execução](docs/contributing.md#executar-localmente) antes de configurar outro banco ou perfil.

## Links rápidos

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- Especificação OpenAPI: `http://localhost:8080/v3/api-docs`
- Health check: `http://localhost:8080/actuator/health`
- [Exemplos de requisições e respostas](docs/api-examples.md)
- [Arquitetura e diagrama](docs/architecture.md)
- [Módulos e responsabilidades](docs/modules.md)
- [Fluxos de negócio](docs/business-flows.md)
- [Guia de contribuição, testes e Docker](docs/contributing.md)

## API em resumo

| Domínio | Endpoints |
| --- | --- |
| Empresas | `POST /api/v1/empresas` |
| Lojas | `POST /api/v1/lojas`, `GET /api/v1/lojas?empresaId=` |
| Produtos | `POST /api/v1/produtos`, `GET /api/v1/produtos/{id}` |
| Clientes e usuários | `POST /api/v1/clientes`, `POST /api/v1/usuarios` |
| Caixa | `POST /api/v1/caixas`, abertura e fechamento de sessão |
| Estoque | `POST /api/v1/estoque/movimentacoes` |
| Vendas | `POST /api/v1/vendas/finalizar`, `GET /api/v1/vendas/{id}` |
| Dashboard | `GET /api/v1/dashboard` |

Todas as rotas de negócio usam o prefixo `/api/v1`. As respostas seguem o envelope `ApiResponse`; erros incluem `code`, `message`, `traceId` e, quando aplicável, os campos inválidos.

## Execução com Docker

Para subir aplicação e PostgreSQL juntos:

```bash
cp .env.example .env
docker compose up --build
```

O Compose expõe a API em `8080` e o PostgreSQL em `5432` por padrão. Nunca versione credenciais reais no `.env`; para produção, use variáveis de ambiente do provedor de execução.

## Testes

```bash
mvn test
```

O relatório JaCoCo é criado em `target/site/jacoco/index.html`. Os testes de integração usam PostgreSQL com Testcontainers e executam automaticamente quando Docker estiver disponível.

## Segurança atual

Enquanto não há uma configuração de segurança específica no projeto, a presença do Spring Security ativa a configuração padrão do Spring Boot. Em desenvolvimento, use Basic Auth com usuário `user` e a senha temporária exibida no log de inicialização. Essa configuração é apenas transitória e deve ser substituída por autenticação explícita antes de produção.
