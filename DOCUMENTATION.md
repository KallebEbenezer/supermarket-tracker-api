# 🛒 Supermarket Tracker — Backend Documentation

> **Para agentes de IA**: Esta documentação mapeia o backend inteiro com caminhos de arquivo,
> arquitetura e fluxos. Consulte-a antes de explorar o código para economizar tokens.

---

## 1. Visão Geral

| Item | Detalhe |
|------|---------|
| **Framework** | Spring Boot 3.5.16 |
| **Java** | 21 |
| **Banco** | PostgreSQL (via JPA/Hibernate) |
| **Migrações** | Flyway (V1–V16) |
| **Auth** | JWT (access 15min + refresh 7d) |
| **Pagamentos** | MercadoPago (PIX + Cartão) |
| **WebSocket** | Spring WebSocket (pagamentos em tempo real) |
| **Docs API** | SpringDoc OpenAPI (Swagger UI) |
| **Build** | Maven (`pom.xml`) |

---

## 2. Estrutura de Pacotes

```
com.supermarkettracker
├── SupermarketTrackerApplication.java          ← Entry point
├── application/                                 ← Casos de uso, commands, queries, DTOs
│   ├── command/                                  ← Commands (21 classes) - CQRS write side
│   ├── query/                                    ← Queries (15 classes) - CQRS read side
│   ├── dto/                                      ← DTOs de transferência
│   ├── mapper/                                   ← Mappers de application layer
│   ├── service/                                  ← TokenService (JWT)
│   ├── usecase/                                  ← Use Cases (40+ classes)
│   └── validator/                                ← Validações de domínio
├── domain/                                       ← Regras de negócio puras
│   ├── exception/                                ← Exceções de domínio
│   ├── gateway/                                  ← Portas de saída (gateways)
│   ├── model/                                    ← Modelos de domínio + enums + value objects
│   └── repository/                               ← Interfaces de repositório (portas de entrada)
└── infrastructure/                               ← Implementações técnicas
    ├── config/                                   ← Security, JWT, OpenAPI, Audit config
    ├── integration/                              ← Adaptadores externos
    │   ├── mercadopago/                           ← MercadoPago (PIX, Cartão, Webhook)
    │   ├── mock/                                  ← Mocks para dev/testes
    │   ├── pix/                                   ← Payload builder PIX
    │   └── smtp/                                  ← Email SMTP
    ├── persistence/                              ← JPA persistence layer
    │   ├── adapter/                               ← Repository adapters (JPA → Domain)
    │   ├── converter/                             ← Conversores JPA
    │   ├── entity/                                ← JPA Entities (18 classes)
    │   ├── filter/                                ← Filtros de consulta
    │   ├── mapper/                                ← Persistence mappers
    │   └── repository/                            ← Spring Data JPA repositories
    ├── web/                                      ← REST API layer
    │   ├── controller/                            ← Controllers REST (15 classes)
    │   ├── dto/                                   ← Response DTOs
    │   ├── logging/                               ← Request logging/audit
    │   ├── request/                               ← Request DTOs
    │   └── security/                              ← JWT filter
    └── websocket/                                ← WebSocket handlers
```

---

## 3. Arquitetura

**Padrão**: Hexagonal Architecture (Ports & Adapters) com CQRS leve.

```
HTTP Request
    ↓
┌─────────────────────────────────────────────┐
│  INFRASTRUCTURE                             │
│  ┌──────────────┐  ┌──────────────────┐     │
│  │ Controller   │→ │ Command/Query    │     │
│  │ (web/)       │  │ (application/)   │     │
│  └──────────────┘  └───────┬──────────┘     │
│                            ↓                │
│                    ┌──────────────┐          │
│                    │ UseCase      │          │
│                    │ (usecase/)   │          │
│                    └──────┬───────┘          │
│                           ↓                 │
│                    ┌──────────────┐          │
│                    │ Repository   │ (port)   │
│                    │ (domain/)    │          │
│                    └──────┬───────┘          │
│                           ↓                 │
│                    ┌──────────────┐          │
│                    │ JPA Adapter  │          │
│                    │ (persistence/)│          │
│                    └──────┬───────┘          │
│                           ↓                 │
│                       PostgreSQL            │
└─────────────────────────────────────────────┘
```

---

## 4. Controllers e Endpoints REST

Base URL: `http://localhost:8080`

### Autenticação (`/api/v1/auth`)

| Método | Endpoint | Controller | Descrição |
|--------|----------|------------|-----------|
| POST | `/api/v1/auth/login` | `AutenticacaoController` | Login com email/senha |
| POST | `/api/v1/auth/registrar` | `AutenticacaoController` | Registro de novo usuário |
| POST | `/api/v1/auth/refresh` | `AutenticacaoController` | Renovar access token |
| POST | `/api/v1/auth/solicitar-reset-senha` | `AutenticacaoController` | Solicitar reset de senha |
| POST | `/api/v1/auth/redefinir-senha` | `AutenticacaoController` | Redefinir senha com token |

### Empresas (`/api/v1/empresas`)

| Método | Endpoint | Controller | Descrição |
|--------|----------|------------|-----------|
| POST | `/api/v1/empresas` | `EmpresaController` | Cadastrar empresa |
| GET | `/api/v1/empresas` | `EmpresaController` | Listar empresas do usuário |
| GET | `/api/v1/empresas/{id}` | `EmpresaController` | Buscar empresa por ID |
| PUT | `/api/v1/empresas/{id}` | `EmpresaController` | Atualizar empresa |

### Lojas (`/api/v1/lojas`)

| Método | Endpoint | Controller | Descrição |
|--------|----------|------------|-----------|
| POST | `/api/v1/lojas` | `LojaController` | Cadastrar loja |
| GET | `/api/v1/lojas` | `LojaController` | Listar lojas da empresa |
| GET | `/api/v1/lojas/{id}` | `LojaController` | Buscar loja por ID |
| PUT | `/api/v1/lojas/{id}` | `LojaController` | Atualizar loja |
| DELETE | `/api/v1/lojas/{id}` | `LojaController` | Excluir loja |

### Produtos (`/api/v1/produtos`)

| Método | Endpoint | Controller | Descrição |
|--------|----------|------------|-----------|
| POST | `/api/v1/produtos` | `ProdutoController` | Cadastrar produto |
| GET | `/api/v1/produtos` | `ProdutoController` | Listar produtos (com filtros) |
| GET | `/api/v1/produtos/{id}` | `ProdutoController` | Buscar produto por ID |
| PUT | `/api/v1/produtos/{id}` | `ProdutoController` | Atualizar produto |
| DELETE | `/api/v1/produtos/{id}` | `ProdutoController` | Excluir produto |

### Clientes (`/api/v1/clientes`)

| Método | Endpoint | Controller | Descrição |
|--------|----------|------------|-----------|
| POST | `/api/v1/clientes` | `ClienteController` | Cadastrar cliente |
| GET | `/api/v1/clientes` | `ClienteController` | Listar clientes |
| GET | `/api/v1/clientes/{id}` | `ClienteController` | Buscar cliente por ID |
| PUT | `/api/v1/clientes/{id}` | `ClienteController` | Atualizar cliente |
| DELETE | `/api/v1/clientes/{id}` | `ClienteController` | Excluir cliente |

### Usuários (`/api/v1/usuarios`)

| Método | Endpoint | Controller | Descrição |
|--------|----------|------------|-----------|
| POST | `/api/v1/usuarios` | `UsuarioController` | Cadastrar usuário (admin) |
| GET | `/api/v1/usuarios` | `UsuarioController` | Listar usuários da empresa |
| GET | `/api/v1/usuarios/{id}` | `UsuarioController` | Buscar usuário por ID |
| PUT | `/api/v1/usuarios/{id}` | `UsuarioController` | Atualizar usuário |
| DELETE | `/api/v1/usuarios/{id}` | `UsuarioController` | Excluir usuário |

### Caixas (`/api/v1/caixas`)

| Método | Endpoint | Controller | Descrição |
|--------|----------|------------|-----------|
| POST | `/api/v1/caixas` | `CaixaController` | Cadastrar caixa |
| GET | `/api/v1/caixas` | `CaixaController` | Listar caixas da loja |
| GET | `/api/v1/caixas/{id}` | `CaixaController` | Buscar caixa por ID |
| PUT | `/api/v1/caixas/{id}` | `CaixaController` | Atualizar caixa |
| DELETE | `/api/v1/caixas/{id}` | `CaixaController` | Excluir caixa |

### Sessão de Caixa (`/api/v1/sessoes-caixa`)

| Método | Endpoint | Controller | Descrição |
|--------|----------|------------|-----------|
| POST | `/api/v1/sessoes-caixa/abrir` | `CaixaController` | Abrir sessão de caixa |
| POST | `/api/v1/sessoes-caixa/fechar` | `CaixaController` | Fechar sessão de caixa |
| GET | `/api/v1/sessoes-caixa/atual` | `CaixaController` | Buscar sessão atual |

### Vendas (`/api/v1/vendas`)

| Método | Endpoint | Controller | Descrição |
|--------|----------|------------|-----------|
| POST | `/api/v1/vendas` | `VendaController` | Iniciar nova venda |
| GET | `/api/v1/vendas` | `VendaController` | Listar vendas |
| GET | `/api/v1/vendas/{id}` | `VendaController` | Buscar venda por ID |
| POST | `/api/v1/vendas/{id}/itens` | `VendaController` | Adicionar item à venda |
| POST | `/api/v1/vendas/{id}/finalizar` | `VendaController` | Finalizar venda |

### Checkout e Pagamento (`/api/v1/checkout`)

| Método | Endpoint | Controller | Descrição |
|--------|----------|------------|-----------|
| POST | `/api/v1/checkout/pagamento` | `VendaController` | Processar pagamento (PIX/Cartão/Dinheiro) |

### Estoque (`/api/v1/estoque`)

| Método | Endpoint | Controller | Descrição |
|--------|----------|------------|-----------|
| POST | `/api/v1/estoque/movimentacao` | `EstoqueController` | Registrar movimentação de estoque |
| GET | `/api/v1/estoque/movimentacoes` | `EstoqueController` | Listar movimentações |

### Contas Bancárias (`/api/v1/contas-bancarias`)

| Método | Endpoint | Controller | Descrição |
|--------|----------|------------|-----------|
| POST | `/api/v1/contas-bancarias` | `ContaBancariaController` | Cadastrar conta bancária |
| GET | `/api/v1/contas-bancarias` | `ContaBancariaController` | Listar contas bancárias |
| GET | `/api/v1/contas-bancarias/{id}` | `ContaBancariaController` | Buscar conta bancária |
| PUT | `/api/v1/contas-bancarias/{id}` | `ContaBancariaController` | Atualizar conta bancária |
| DELETE | `/api/v1/contas-bancarias/{id}` | `ContaBancariaController` | Excluir conta bancária |

### Dashboard (`/api/v1/dashboard`)

| Método | Endpoint | Controller | Descrição |
|--------|----------|------------|-----------|
| GET | `/api/v1/dashboard` | `DashboardController` | Dados consolidados do dashboard |
| GET | `/api/v1/dashboard/resumo` | `DashboardController` | Resumo de vendas |
| GET | `/api/v1/dashboard/produtos-mais-vendidos` | `DashboardController` | Top produtos vendidos |
| GET | `/api/v1/dashboard/ultima-venda` | `DashboardController` | Última venda realizada |
| GET | `/api/v1/dashboard/estoque-baixo` | `DashboardController` | Produtos com estoque baixo |

### Cartão NFC (`/api/v1/cartao-nfc`)

| Método | Endpoint | Controller | Descrição |
|--------|----------|------------|-----------|
| POST | `/api/v1/cartao-nfc/processar` | `CartaoNfcController` | Processar pagamento por cartão NFC |

### Webhooks PIX (`/webhooks/pix`)

| Método | Endpoint | Controller | Descrição |
|--------|----------|------------|-----------|
| POST | `/webhooks/pix` | `PixWebhookController` | Receber notificação de pagamento PIX |
| POST | `/webhooks/pix/mock-confirm/{idVenda}` | `PixMockConfirmController` | Mock para confirmar PIX (dev) |

---

## 5. WebSocket

| Endpoint | Handler | Descrição |
|----------|---------|-----------|
| `/ws/pagamentos` | `PaymentWebSocketHandler` | Status de pagamentos em tempo real |
| `/ws/pix` | `PixWebSocketHandler` | Notificações de pagamento PIX |

---

## 6. Modelos de Domínio

### Entidades Principais

| Modelo | Arquivo | Descrição |
|--------|---------|-----------|
| `Empresa` | `domain/model/Empresa.java` | Empresa/dono do sistema |
| `Usuario` | `domain/model/Usuario.java` | Usuário do sistema (com papel/permissões) |
| `Loja` | `domain/model/Loja.java` | Loja dentro de uma empresa |
| `Caixa` | `domain/model/Caixa.java` | Terminal de caixa |
| `SessaoCaixa` | `domain/model/SessaoCaixa.java` | Sessão aberta de caixa |
| `Produto` | `domain/model/Produto.java` | Produto cadastrado |
| `CategoriaProduto` | `domain/model/CategoriaProduto.java` | Categoria de produto |
| `Cliente` | `domain/model/Cliente.java` | Cliente/cadastro do cliente |
| `Venda` | `domain/model/Venda.java` | Venda realizada |
| `ItemVenda` | `domain/model/ItemVenda.java` | Item dentro de uma venda |
| `Pagamento` | `domain/model/Pagamento.java` | Pagamento de uma venda |
| `PagamentoPix` | `domain/model/PagamentoPix.java` | Pagamento PIX específico |
| `PagamentoCartao` | `domain/model/PagamentoCartao.java` | Pagamento cartão específico |
| `MovimentacaoEstoque` | `domain/model/MovimentacaoEstoque.java` | Entrada/saída de estoque |
| `ContaBancaria` | `domain/model/ContaBancaria.java` | Conta bancária vinculada |
| `Credencial` | `domain/model/Credencial.java` | Credenciais de acesso |
| `EmpresaUsuario` | `domain/model/EmpresaUsuario.java` | Relação empresa-usuário |

### Enums

| Enum | Arquivo | Valores |
|------|---------|---------|
| `PapelUsuario` | `enums/PapelUsuario.java` | Papéis do usuário |
| `StatusAtivo` | `enums/StatusAtivo.java` | ATIVO/INATIVO |
| `StatusEmpresa` | `enums/StatusEmpresa.java` | Status da empresa |
| `StatusVenda` | `enums/StatusVenda.java` | Status da venda |
| `StatusPagamento` | `enums/StatusPagamento.java` | Status do pagamento |
| `StatusSessaoCaixa` | `enums/StatusSessaoCaixa.java` | Status da sessão de caixa |
| `TipoPagamento` | `enums/TipoPagamento.java` | PIX, CARTAO, DINHEIRO |
| `ModalidadeCartao` | `enums/ModalidadeCartao.java` | Modalidade do cartão |
| `TipoMovimentacaoEstoque` | `enums/TipoMovimentacaoEstoque.java` | ENTRADA/SAÍDA |
| `TipoContaBancaria` | `enums/TipoContaBancaria.java` | Tipo da conta bancária |

### Value Objects

| VO | Arquivo | Descrição |
|----|---------|-----------|
| `Dinheiro` | `valueobject/Dinheiro.java` | Valor monetário (BigDecimal) |
| `Quantidade` | `valueobject/Quantidade.java` | Quantidade não-negativa |
| `Documento` | `valueobject/Documento.java` | CPF/CNPJ |
| `Email` | `valueobject/Email.java` | Email validado |
| `Endereco` | `valueobject/Endereco.java` | Endereço completo |
| `Identificador` | `valueobject/Identificador.java` | ID único (UUID) |

---

## 7. Exceções de Domínio

| Exceção | Descrição |
|---------|-----------|
| `CredenciaisInvalidasException` | Email/senha inválidos |
| `EntidadeNaoEncontradaException` | Entidade não encontrada |
| `ConflitoDeDominioException` | Conflito (ex: email duplicado) |
| `RegraDeDominioException` | Violação de regra de negócio |
| `TokenRedefinicaoInvalidoException` | Token de reset inválido |
| `GatewayIndisponivelException` | Serviço externo indisponível |
| `GatewayConfiguracaoException` | Configuração de gateway incorreta |

---

## 8. Gateways (Portas de Saída)

| Gateway | Arquivo | Descrição |
|---------|---------|-----------|
| `AutenticacaoGateway` | `gateway/AutenticacaoGateway.java` | Autenticação |
| `EmailGateway` | `gateway/EmailGateway.java` | Envio de emails |
| `NotificacaoGateway` | `gateway/NotificacaoGateway.java` | Notificações push |
| `PixGateway` | `gateway/PixGateway.java` | Pagamento PIX |
| `ProcessadorPagamentoGateway` | `gateway/ProcessadorPagamentoGateway.java` | Processador de pagamento |
| `CartaoGateway` | `gateway/CartaoGateway.java` | Pagamento por cartão |
| `StorageGateway` | `gateway/StorageGateway.java` | Armazenamento de arquivos |
| `ArmazenamentoImagemGateway` | `gateway/ArmazenamentoImagemGateway.java` | Armazenamento de imagens |
| `PixPaymentEventPublisher` | `gateway/PixPaymentEventPublisher.java` | Publicação de eventos PIX |

### Implementações

**MercadoPago** (produção):
- `MercadoPagoCartaoAdapter` — Pagamento por cartão
- `MercadoPagoPixAdapter` — Pagamento PIX
- `MercadoPagoProcessadorPagamentoAdapter` — Processador unificado
- `MercadoPagoWebhookValidator` — Validação de webhooks

**Mock** (desenvolvimento/testes):
- `CartaoMockAdapter`, `NotificacaoMockAdapter`, `EmailMockAdapter`, `ProcessadorPagamentoMockAdapter`, `StorageMockAdapter`, `ArmazenamentoImagemMockAdapter`

---

## 9. JPA Entities (Persistence Layer)

| Entity | Tabela | Arquivo |
|--------|--------|---------|
| `EmpresaEntity` | `empresas` | `persistence/entity/EmpresaEntity.java` |
| `UsuarioEntity` | `usuarios` | `persistence/entity/UsuarioEntity.java` |
| `LojaEntity` | `lojas` | `persistence/entity/LojaEntity.java` |
| `CaixaEntity` | `caixas` | `persistence/entity/CaixaEntity.java` |
| `SessaoCaixaEntity` | `sessoes_caixa` | `persistence/entity/SessaoCaixaEntity.java` |
| `ProdutoEntity` | `produtos` | `persistence/entity/ProdutoEntity.java` |
| `CategoriaProdutoEntity` | `categorias_produto` | `persistence/entity/CategoriaProdutoEntity.java` |
| `ClienteEntity` | `clientes` | `persistence/entity/ClienteEntity.java` |
| `VendaEntity` | `vendas` | `persistence/entity/VendaEntity.java` |
| `ItemVendaEntity` | `itens_venda` | `persistence/entity/ItemVendaEntity.java` |
| `PagamentoEntity` | `pagamentos` | `persistence/entity/PagamentoEntity.java` |
| `PagamentoPixEntity` | — | `persistence/entity/PagamentoPixEntity.java` |
| `PagamentoCartaoEntity` | — | `persistence/entity/PagamentoCartaoEntity.java` |
| `MovimentacaoEstoqueEntity` | `movimentacoes_estoque` | `persistence/entity/MovimentacaoEstoqueEntity.java` |
| `ContaBancariaEntity` | `contas_bancarias` | `persistence/entity/ContaBancariaEntity.java` |
| `CredencialEntity` | `credenciais` | `persistence/entity/CredencialEntity.java` |
| `EmpresaUsuarioEntity` | `empresas_usuarios` | `persistence/entity/EmpresaUsuarioEntity.java` |
| `DashboardCacheDiarioEntity` | — | `persistence/entity/DashboardCacheDiarioEntity.java` |

---

## 10. Segurança

- **JWT Filter**: `JwtAuthenticationFilter` intercepta requests, valida o token e popula o SecurityContext.
- **Security Config**: `SecurityConfiguration` configura o `SecurityFilterChain`.
- **CORS**: Configurado no SecurityConfig.
- **Endpoints públicos**: `/api/v1/auth/**`, `/webhooks/**`, `/ws/**`, Swagger (`/swagger-ui/**`, `/v3/api-docs/**`).
- **JWT Properties** (`JwtProperties`): secret, access-ttl (15min), refresh-ttl (7d).

---

## 11. Configuração e Ambientes

### Profiles
- **dev** (padrão): `application-dev.yml`
- **prod**: `application-prod.yml`

### Variáveis de Ambiente (Obrigatórias)

| Variável | Descrição |
|----------|-----------|
| `APP_JWT_SECRET` | Chave secreta para JWT |
| `MAIL_HOST` | Host SMTP (default: smtp.gmail.com) |
| `MAIL_PORT` | Porta SMTP (default: 587) |
| `MAIL_USERNAME` | Usuário SMTP |
| `MAIL_PASSWORD` | Senha SMTP |
| `PIX_MERCADOPAGO_ACCESS_TOKEN` | Token MercadoPago (PIX) |
| `MERCADOPAGO_ACCESS_TOKEN` | Token MercadoPago (geral) |
| `MERCADOPAGO_WEBHOOK_SECRET` | Secret para webhooks |
| `PIX_MOCK_ENABLED` | Mock PIX habilitado (default: true) |
| `CARTAO_PROVIDER` | Provider de cartão (default: mercadopago) |
| `EMAIL_PROVIDER` | Provider de email (default: smtp) |
| `EMAIL_FROM` | Email remetente |
| `SERVER_PORT` | Porta do servidor (default: 8080) |

### Configuração Spring (`application.yml`)
- `spring.profiles.default: dev`
- `spring.jpa.hibernate.ddl-auto: validate`
- `spring.flyway.enabled: true`
- JWT: access 15min, refresh 7 days
- Actuator: health + info endpoints

---

## 12. Migrações Flyway

| Migração | Descrição |
|----------|-----------|
| `V1` | Extensions, enums do PostgreSQL |
| `V2` | Empresas e usuários |
| `V3` | Lojas e caixas |
| `V4` | Cadastros comerciais (produtos, clientes) |
| `V5` | Vendas e itens de venda |
| `V6` | Pagamentos |
| `V7` | Movimentações de estoque |
| `V8` | Cache de dashboard |
| `V9` | Integridade referencial e funções |
| `V10` | Dashboard produtos diário |
| `V11` | Índices para consultas |
| `V12` | Índices busca produto e dashboard |
| `V13` | CNPJ como varchar |
| `V14` | Credenciais |
| `V15` | Pagamento PIX expiração |
| `V16` | Movimentação estoque única por venda |

---

## 13. Como Rodar

```bash
# Docker Compose (recomendado)
docker-compose up -d

# Maven (desenvolvimento)
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Testes
mvn test

# Build
mvn clean package

# Swagger UI
http://localhost:8080/swagger-ui/index.html
```

---

## 14. Testes

- **Framework**: JUnit 5 + Testcontainers (PostgreSQL)
- **Coverage mínimo**: 80% linhas em `application/usecase/*` (JaCoCo)
- **Mocks de integração**: Adapters mock para MercadoPago, Email, Notificação, etc.

---

## 15. Guia para Agentes de IA

### Onde encontrar o que

| Quer fazer... | Olhar em... |
|---------------|-------------|
| Criar novo endpoint | Criar Command/Query em `application/command/` ou `application/query/`, UseCase em `usecase/`, Controller em `infrastructure/web/controller/` |
| Criar nova entidade | Modelo em `domain/model/`, Entity em `infrastructure/persistence/entity/`, Migration em `resources/db/migration/`, Repository em `domain/repository/`, JPA Adapter em `infrastructure/persistence/adapter/` |
| Modificar regra de negócio | UseCase em `application/usecase/` |
| Modificar persistência | JPA Entity em `persistence/entity/`, Repository em `persistence/repository/` |
| Adicionar gateway externo | Interface em `domain/gateway/`, implementação em `infrastructure/integration/` |
| Alterar autenticação | `SecurityConfiguration`, `JwtProperties`, `TokenService`, `JwtAuthenticationFilter` |
| Alterar validação | `application/validator/Validador.java` |

### Padrões de código

- **CQRS**: Commands (escrita) separados de Queries (leitura)
- **Mapper MapStruct**: Conversão automática entre Domain ↔ Entity, Domain ↔ DTO
- **Exceptions de domínio**: São capturadas pelo `GlobalExceptionHandler` e convertidas em respostas HTTP apropriadas
- **Request/Response DTOs**: Na camada web, nunca expor entidades diretamente

### Fluxo típico de uma operação

```
Controller (infrastructure/web/controller/)
  → Command ou Query (application/command/ ou query/)
    → UseCase (application/usecase/)
      → Repository interface (domain/repository/)
        → JPA Adapter (infrastructure/persistence/adapter/)
          → Spring Data Repository (infrastructure/persistence/repository/)
            → PostgreSQL
```

---

*Documento gerado automaticamente. Última atualização: 2026-09-03.*
