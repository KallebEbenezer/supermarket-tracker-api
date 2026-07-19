package com.supermarkettracker.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.DriverManager;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/** Exercita as migrations contra PostgreSQL real, sem substituir o banco por H2. */
@Testcontainers(disabledWithoutDocker = true)
class PostgresMigrationsIntegrationTest {
    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("supermarket_tracker_test")
            .withUsername("supermarket")
            .withPassword("supermarket");

    @Test
    void aplicaMigrationsNoPostgresReal() throws Exception {
        Flyway.configure().dataSource(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())
                .locations("classpath:db/migration").load().migrate();

        try (var connection = DriverManager.getConnection(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword());
             var statement = connection.createStatement();
             var result = statement.executeQuery("select count(*) from information_schema.tables where table_schema = 'public' and table_name = 'empresas'")) {
            result.next();
            assertThat(result.getInt(1)).isEqualTo(1);
        }
    }
}
