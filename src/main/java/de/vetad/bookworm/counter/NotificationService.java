package de.vetad.bookworm.counter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class NotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);

    private final String topic;

    private final KafkaTemplate<Integer, LibraryEvent> kafkaTemplate;

    public NotificationService(
            @Value("${notifications.libraryevent.topic}") String topic,
            KafkaTemplate<Integer, LibraryEvent> kafkaTemplate) {

        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    SendResult<Integer, LibraryEvent> emitBlocking(LibraryEvent libraryEvent) {
        Integer key = 42;

        try {
            return kafkaTemplate.send(topic, key, libraryEvent).get(5, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    CompletableFuture<Void> emitAsync(LibraryEvent libraryEvent) {
        Integer key = 42;

        return kafkaTemplate.send(topic, key, libraryEvent)
                .thenAccept(sendResult -> { });
    }
}
