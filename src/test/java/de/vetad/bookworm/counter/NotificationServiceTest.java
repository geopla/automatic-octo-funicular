package de.vetad.bookworm.counter;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.K;
import org.testcontainers.utility.DockerImageName;

import java.io.Serializable;
import java.util.Map;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationServiceTest {

    static ConfluentKafkaContainer kafka;

    NotificationService notificationService;

    @BeforeAll
    static void beforeAll() {
        startKafkaContainer();
    }

    static void startKafkaContainer() {
        String proxy = System.getenv().getOrDefault("IMAGE_PROXY", "");
        String image = "confluentinc/cp-kafka:7.7.5";
        String imageName = "%s%s".formatted(proxy, image);

        DockerImageName kafkaImageName = DockerImageName
                .parse(imageName)
                .asCompatibleSubstituteFor("confluentinc/cp-kafka");

        kafka = new ConfluentKafkaContainer(kafkaImageName);
        kafka.start();
    }

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService(createKafkaTemplate());
    }

    KafkaTemplate<Integer, String> createKafkaTemplate() {
        Map<String, Object> producerConfig = Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers(),
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class
        );

        DefaultKafkaProducerFactory<Integer, String> producerFactory = new DefaultKafkaProducerFactory<>(producerConfig);

        return new KafkaTemplate<>(producerFactory);
    }

    @AfterAll
    static void afterAll() {
        Logger LOGGER = LoggerFactory.getLogger(NotificationServiceTest.class);
        String kafkaLogs = kafka.getLogs();

        kafka.stop();

        LOGGER.info(kafkaLogs);
    }

    @Test
    @DisplayName("Should produce a library event")
    void shouldProduceLibraryEvent() {
        LibraryEvent libraryEvent = Instancio.of(LibraryEvent.class)
                .create();

        SendResult<Integer, String> sendResult = notificationService.emit(libraryEvent);

        assertThat(sendResult).isNotNull();
    }
}