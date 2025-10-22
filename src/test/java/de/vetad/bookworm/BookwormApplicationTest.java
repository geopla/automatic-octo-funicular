package de.vetad.bookworm;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class BookwormApplicationTest {

    @Container
    static final ConfluentKafkaContainer kafkaContainer = new ConfluentKafkaContainer(Kafka.DOCKER_IMAGE_NAME);

    @Test
    @DisplayName("Should load application context - with all its beans")
    void contextLoads(ApplicationContext context) {
        assertThat(context).isNotNull();
    }

}
