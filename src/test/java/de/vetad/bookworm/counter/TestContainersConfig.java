package de.vetad.bookworm.counter;

import de.vetad.bookworm.Kafka;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.kafka.ConfluentKafkaContainer;

@TestConfiguration
public class TestContainersConfig {

    @Bean
    @ServiceConnection
    public ConfluentKafkaContainer kafkaContainer() {
        return new ConfluentKafkaContainer(Kafka.DOCKER_IMAGE_NAME);
    }

    @Bean
    @ServiceConnection
    public PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>("postgres:17");
    }
}

