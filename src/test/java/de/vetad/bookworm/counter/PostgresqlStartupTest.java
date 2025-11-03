package de.vetad.bookworm.counter;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;

public class PostgresqlStartupTest {

    static PostgreSQLContainer postgres;

    @BeforeAll
    static void beforeAll() {
        postgres = new PostgreSQLContainer("postgres:17");
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @Test
    @DisplayName("Should start the database at all")
    void shouldStartTheDatabaseAtAll() {
        assertThat(postgres).isNotNull();
    }
}
