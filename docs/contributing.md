# Desenvolvimento e contribuição

## Executar localmente

1. Instale Java 21 e Maven 3.9+.
2. Inicie PostgreSQL localmente ou execute `docker compose up -d postgres`.
3. Defina `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME` e `DB_PASSWORD` se não usar os valores padrão do perfil `dev`.
4. Execute `mvn spring-boot:run`.

O perfil padrão é `dev`; ele usa `localhost:5432/supermarket_tracker`, usuário e senha `supermarket`. Para outro perfil, use `SPRING_PROFILES_ACTIVE=prod` e forneça as variáveis de banco apropriadas.

## Docker Compose

```bash
cp .env.example .env
docker compose up --build
docker compose logs -f app
docker compose down
```

O Compose usa o perfil `dev` por padrão e conecta a API exclusivamente ao PostgreSQL local do próprio Compose. Para executar contra infraestrutura externa, inicie a aplicação fora do Compose com `SPRING_PROFILES_ACTIVE=prod` e as variáveis `DB_*`; o perfil `prod` usa SSL.

Para remover também o volume local do banco, execute `docker compose down -v`. Isso apaga os dados do ambiente Docker local.

## Banco e migrations

- Crie migrations no formato `V<numero>__descricao_em_snake_case.sql`.
- Não altere migrations que já possam ter sido executadas em outro ambiente.
- Prefira constraints e índices que expressem invariantes do domínio.
- Valide migrations pelo teste de integração com Docker ativo: `mvn test`.

## Testes e qualidade

```bash
mvn test
```

Testes unitários usam JUnit 5, AssertJ e Mockito. O Testcontainers inicia PostgreSQL para validar migrations quando Docker está disponível; sem Docker, esse teste é ignorado. O JaCoCo exige ao menos 80% de linhas cobertas no pacote de casos de uso e gera o relatório em `target/site/jacoco/index.html`.

## Convenções

- Mantenha o domínio livre de dependências de framework.
- Adicione casos de teste para cada novo caso de uso e para fluxos de erro relevantes.
- Use commands para escritas e queries para leituras.
- Não exponha entidades JPA diretamente no HTTP.
- Não inclua senhas, tokens, URLs privadas ou dumps de banco em commits. Use `.env.example` para documentar variáveis.
- Antes de abrir um PR, execute `mvn test` e `git diff --check`.
