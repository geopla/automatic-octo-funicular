package de.vetad.bookworm.counter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTestWithContainers
public class PostgresqlStartupIT {

    @Autowired
    JdbcConnectionDetails connectionDetails;

    @Test
    @DisplayName("Should inject PostgreSQL connection details")
    void shouldInjectConnectionDetails() {
        assertThat(connectionDetails).isNotNull();
    }
}
