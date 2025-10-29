//package de.vetad.bookworm.counter;
//
//import de.vetad.bookworm.Kafka;
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.apache.kafka.clients.consumer.ConsumerRecords;
//import org.apache.kafka.clients.consumer.KafkaConsumer;
//import org.apache.kafka.clients.producer.ProducerConfig;
//import org.apache.kafka.common.serialization.IntegerDeserializer;
//import org.apache.kafka.common.serialization.IntegerSerializer;
//import org.instancio.Instancio;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.kafka.core.DefaultKafkaProducerFactory;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.support.serializer.JsonDeserializer;
//import org.springframework.kafka.support.serializer.JsonSerializer;
//import org.testcontainers.kafka.ConfluentKafkaContainer;
//
//import java.time.Duration;
//import java.time.temporal.ChronoUnit;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicLong;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//class NotificationServiceTest {
//
//    static ConfluentKafkaContainer kafka;
//
//    KafkaTemplate<Integer, LibraryEvent> kafkaTemplate;
//
//    String topicBaseName = "bookwork.libraryevent";
//    AtomicLong topicNumber = new AtomicLong(0);
//
//    @BeforeAll
//    static void beforeAll() {
//        startKafkaContainer();
//    }
//
//    static void startKafkaContainer() {
//        kafka = new ConfluentKafkaContainer(Kafka.DOCKER_IMAGE_NAME);
//        kafka.start();
//    }
//
//    @BeforeEach
//    void setUp() {
//        kafkaTemplate = createKafkaTemplate(kafka.getBootstrapServers());
//    }
//
//    KafkaTemplate<Integer, LibraryEvent> createKafkaTemplate(String bootstrapServers) {
//        Map<String, Object> producerConfig = Map.of(
//                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
//                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class,
//                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
//        );
//        DefaultKafkaProducerFactory<Integer, LibraryEvent> producerFactory = new DefaultKafkaProducerFactory<>(producerConfig);
//
//        return new KafkaTemplate<>(producerFactory);
//    }
//
//    @AfterAll
//    static void afterAll() {
//        Logger LOGGER = LoggerFactory.getLogger(NotificationServiceTest.class);
//        String kafkaLogs = kafka.getLogs();
//
//        kafka.stop();
//
//        LOGGER.info(kafkaLogs);
//    }
//
//    @Test
//    @DisplayName("Should produce AND consume a library event - blocking")
//    void shouldProduceLibraryEvent() {
//        String topic = nextTopicName();
//        NotificationService notificationService = createNotificationServiceWith(topic);
//
//        LibraryEvent libraryEvent = Instancio.of(LibraryEvent.class).create();
//
//        notificationService.emitBlocking(libraryEvent);
//
//        try (KafkaConsumer<Integer, LibraryEvent> kafkaConsumer = createKafkaConsumer(topic)) {
//            kafkaConsumer.subscribe(List.of(topic));
//            ConsumerRecords<Integer, LibraryEvent> records = kafkaConsumer.poll(Duration.of(2, ChronoUnit.SECONDS));
//
//            // guard granting exclusivity of topic within that single test
//            assertThat(records.count()).isEqualTo(1);
//
//            ConsumerRecord<Integer, LibraryEvent> record = records.iterator().next();
//            assertThat(record.value()).isEqualTo(libraryEvent);
//        }
//    }
//
//    @Test
//    @DisplayName("Should produce AND consume a library event - asynchronous")
//    void shouldProduceLibraryEventAsync() {
//        String topic = nextTopicName();
//        NotificationService notificationService = createNotificationServiceWith(topic);
//
//        LibraryEvent libraryEvent = Instancio.of(LibraryEvent.class).create();
//
//        CompletableFuture<Void> emitting = notificationService.emitAsync(libraryEvent);
//
//        assertThat(emitting).succeedsWithin(2, TimeUnit.SECONDS);
//
//        // just a demo of how to use a KafkaConsumer directly instead of Spring Boot Kafka test utils
//        try (KafkaConsumer<Integer, LibraryEvent> kafkaConsumer = createKafkaConsumer(topic)) {
//            kafkaConsumer.subscribe(List.of(topic));
//            ConsumerRecords<Integer, LibraryEvent> records = kafkaConsumer.poll(Duration.of(2, ChronoUnit.SECONDS));
//
//            // guard granting exclusivity of topic within that single test
//            assertThat(records.count()).isEqualTo(1);
//
//            ConsumerRecord<Integer, LibraryEvent> record = records.iterator().next();
//            assertThat(record.value()).isEqualTo(libraryEvent);
//        }
//    }
//
//    NotificationService createNotificationServiceWith(String topicName) {
//        // note that this works only with topic autocreation feature enabled
//        return new NotificationService(topicName, kafkaTemplate);
//    }
//
//    String nextTopicName() {
//        return "%s-%d".formatted(topicBaseName, topicNumber.getAndIncrement());
//    }
//
//    KafkaConsumer<Integer, LibraryEvent> createKafkaConsumer(String topic) {
//        return new KafkaConsumer<>(consumerProperties(topic));
//    }
//
//    Map<String, Object> consumerProperties(String topic) {
//        var groupId = "%s.CG".formatted(topic);
//
//        return Map.of(
//                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers(),
//                ConsumerConfig.GROUP_ID_CONFIG, groupId,
//                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class,
//                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class,
//                "spring.json.value.default.type", LibraryEvent.class,
//                // because consumer subscribes after producing a message
//                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"
//        );
//    }
//}