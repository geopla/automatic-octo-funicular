package de.vetad.bookworm.counter;

import de.vetad.bookworm.Kafka;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;

import java.util.Map;

import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
public class KafkaTemplateConfigurationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17");

    @Container
    @ServiceConnection
    static final ConfluentKafkaContainer kafkaContainer = new ConfluentKafkaContainer(Kafka.DOCKER_IMAGE_NAME);


    @Autowired
    KafkaTemplate<Integer, LibraryEvent> kafkaTemplate;

    @Test
    @DisplayName("Should have KafkaTemplate configured with key and value serializers")
    void shouldHaveKafkaTemplate() {
        Map<String, Object> configurationProperties = kafkaTemplate.getProducerFactory().getConfigurationProperties();
        assertThat(configurationProperties.get(KEY_SERIALIZER_CLASS_CONFIG)).isEqualTo(IntegerSerializer.class);
        assertThat(configurationProperties.get(VALUE_SERIALIZER_CLASS_CONFIG)).isEqualTo(JsonSerializer.class);
    }
}
