package de.vetad.bookworm.counter;


import de.vetad.bookworm.Kafka;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaConnectionDetails;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
public class LibraryEventsControllerIT {

    // TODO Switch from container management by JUnit extension to bean managed

    @Container
    @ServiceConnection
    static final ConfluentKafkaContainer kafkaContainer = new ConfluentKafkaContainer(Kafka.DOCKER_IMAGE_NAME);

    @Autowired
    KafkaConnectionDetails connectionDetails;

    @Test
    @DisplayName("Should work at all")
    void shouldWorkAtAll() {
        assertThat(connectionDetails).isNotNull();
    }
}
