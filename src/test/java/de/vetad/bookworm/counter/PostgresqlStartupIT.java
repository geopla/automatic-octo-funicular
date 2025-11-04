package de.vetad.bookworm.counter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
public class PostgresqlStartupIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("bookworm");

    @Autowired
    JdbcClient jdbcClient;

    @Test
    @DisplayName("Should inject PostgreSQL connection details")
    void shouldInjectConnectionDetails() {
        Optional<Integer> tableCount = jdbcClient.sql("SELECT COUNT(*) FROM information_schema.tables WHERE table_name = ?")
                .param("users")
                .query(Integer.class)
                .optional();

        assertThat(tableCount).isNotEmpty();
        assertThat(tableCount.get()).isEqualTo(1);
    }
}
