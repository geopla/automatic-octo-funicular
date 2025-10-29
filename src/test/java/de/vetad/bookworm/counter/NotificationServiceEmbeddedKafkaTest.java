package de.vetad.bookworm.counter;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.kafka.test.utils.KafkaTestUtils.getRecords;

@EmbeddedKafka(kraft = true)
public class NotificationServiceEmbeddedKafkaTest {

    EmbeddedKafkaBroker kafkaBroker;

    KafkaTemplate<Integer, LibraryEvent> kafkaTemplate;

    String topicBaseName = "bookwork.libraryevent";
    AtomicLong topicNumber = new AtomicLong(0);

    @BeforeEach
    void setUp(EmbeddedKafkaBroker kafkaBroker) {
        this.kafkaBroker = kafkaBroker;
        kafkaTemplate = createKafkaTemplate();
    }

    KafkaTemplate<Integer, LibraryEvent> createKafkaTemplate() {
        Map<String, Object> producerConfig = Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBroker.getBrokersAsString(),
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
        );

        DefaultKafkaProducerFactory<Integer, LibraryEvent> producerFactory = new DefaultKafkaProducerFactory<>(producerConfig);

        return new KafkaTemplate<>(producerFactory);
    }

    @Test
    @DisplayName("Should produce AND consume a library event asynchronous")
    void shouldProduceLibraryEventAsync() {
        var uniqueTopicName = createUniqueTopic();
        var notificationService = createNotificationServiceWith(uniqueTopicName);
        var libraryEvent = Instancio.of(LibraryEvent.class).create();

        CompletableFuture<Void> emitting = notificationService.emitAsync(libraryEvent);
        assertThat(emitting).succeedsWithin(2, TimeUnit.SECONDS);

        try (var kafkaConsumer = createKafkaConsumer(uniqueTopicName)) {
            kafkaBroker.consumeFromAnEmbeddedTopic(kafkaConsumer, uniqueTopicName);

            var records = getRecords(kafkaConsumer, Duration.of(2, ChronoUnit.SECONDS));

            assertThat(records.count()).isEqualTo(1);

            ConsumerRecord<Integer, LibraryEvent> record = records.iterator().next();
            assertThat(record.value()).isEqualTo(libraryEvent);
        }
    }

    NotificationService createNotificationServiceWith(String topicName) {
        // note that this works only with topic autocreation feature enabled
        return new NotificationService(topicName, kafkaTemplate);
    }

    String createUniqueTopic() {
        String uniqueTopicName = "%s-%d".formatted(topicBaseName, topicNumber.getAndIncrement());
        kafkaBroker.addTopics(uniqueTopicName);

        return uniqueTopicName;
    }

    KafkaConsumer<Integer, LibraryEvent> createKafkaConsumer(String topic) {
        return new KafkaConsumer<>(consumerProperties(topic));
    }

    Map<String, Object> consumerProperties(String topic) {
        var groupId = "%s.CG".formatted(topic);

        return Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBroker.getBrokersAsString(),
                ConsumerConfig.GROUP_ID_CONFIG, groupId,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class,
                "spring.json.value.default.type", LibraryEvent.class,
                // because consumer might subscribe after producing a message
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"
        );
    }
}
