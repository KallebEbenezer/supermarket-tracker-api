# Supermarket Tracker API

Fundação do backend para o Supermarket Tracker, em Java 21 e Spring Boot, organizada em Clean Architecture.

## Tecnologias

- Java 21, Spring Boot e Maven
- PostgreSQL, Spring Data JPA e Flyway
- Spring Security, Validation, Lombok e MapStruct
- Docker e Docker Compose

## Estrutura

```text
src/main/java/com/supermarkettracker
├── domain/                 # Regras e modelos puros do domínio (ainda vazios)
├── application/
│   ├── port/in/            # Contratos de entrada
│   ├── port/out/           # Contratos de saída
│   └── usecase/            # Casos de uso
└── infrastructure/
    ├── config/             # Configurações técnicas
    ├── persistence/        # Adaptadores JPA
    └── web/                # Adaptadores HTTP
```

Não há entidades, controllers ou casos de uso nesta fundação.

## Executar localmente

É necessário Java 21 e Maven 3.9+.

```bash
mvn spring-boot:run
```

O perfil padrão é `dev` e espera PostgreSQL em `localhost:5432`.

## Executar com Docker

```bash
docker compose up --build
```

A aplicação inicia na porta `8080` e o PostgreSQL na porta `5432`. As variáveis podem ser sobrescritas por um arquivo `.env`, que não é versionado.

## Banco de dados

O Flyway procura migrations em `classpath:db/migration`. O esquema é criado incrementalmente, de `V1__extensions_and_enums.sql` até `V9__integridade_e_funcoes.sql`.
