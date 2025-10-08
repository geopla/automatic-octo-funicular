package de.vetad.bookworm.counter;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationServiceTest {

    static ConfluentKafkaContainer kafka;

    AtomicLong topicNumber = new AtomicLong(0);

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
    @DisplayName("Should produce a library event - blocking")
    void shouldProduceLibraryEvent() {
        String topic = nextTopicName();
        NotificationService notificationService = createNotificationServiceUsing(topic);

        LibraryEvent libraryEvent = Instancio.of(LibraryEvent.class).create();

        notificationService.emitBlocking(libraryEvent);

        try (KafkaConsumer<Integer, String> kafkaConsumer = createKafkaConsumer(topic)) {
            kafkaConsumer.subscribe(List.of(topic));
            ConsumerRecords<Integer, String> records = kafkaConsumer.poll(Duration.of(2, ChronoUnit.SECONDS));

            // guard granting exclusivity of topic within that single test
            assertThat(records.count()).isEqualTo(1);

            ConsumerRecord<Integer, String> record = records.iterator().next();
            assertThat(record.value()).isEqualTo(libraryEvent.toString());
        }
    }

    @Test
    @DisplayName("Should produce a library event - asynchronous")
    void shouldProduceLibraryEventAsync() {
        String topic = nextTopicName();
        NotificationService notificationService = createNotificationServiceUsing(topic);

        LibraryEvent libraryEvent = Instancio.of(LibraryEvent.class).create();

        CompletableFuture<Void> emitting = notificationService.emitAsync(libraryEvent);

        assertThat(emitting).succeedsWithin(2, TimeUnit.SECONDS);

        try (KafkaConsumer<Integer, String> kafkaConsumer = createKafkaConsumer(topic)) {
            kafkaConsumer.subscribe(List.of(topic));
            ConsumerRecords<Integer, String> records = kafkaConsumer.poll(Duration.of(2, ChronoUnit.SECONDS));

            // guard granting exclusivity of topic within that single test
            assertThat(records.count()).isEqualTo(1);

            ConsumerRecord<Integer, String> record = records.iterator().next();
            assertThat(record.value()).isEqualTo(libraryEvent.toString());
        }
    }

    NotificationService createNotificationServiceUsing(String topic) {
        return new NotificationService(topic, createKafkaTemplate());
    }

    String nextTopicName() {
        return "bookworm.libraryevent-%d".formatted(topicNumber.getAndIncrement());
    }

    KafkaConsumer<Integer, String> createKafkaConsumer(String topic) {
        return new KafkaConsumer<>(consumerProperties(topic));
    }

    Map<String, Object> consumerProperties(String topic) {
        var groupId = "%s.CG".formatted(topic);

        return Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers(),
                ConsumerConfig.GROUP_ID_CONFIG, groupId,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                // because consumer subscribes after producing a message
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"
        );
    }
}