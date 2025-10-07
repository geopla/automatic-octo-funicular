package de.vetad.bookworm.counter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    String topic = "bookworm.libraryevent";

    private final KafkaTemplate<Integer, String> kafkaTemplate;

    public NotificationService(KafkaTemplate<Integer, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    SendResult<Integer, String> emit(LibraryEvent libraryEvent) {
        Integer key = 42;
        String value = libraryEvent.toString();

        try {
            return kafkaTemplate.send(topic, key, value).get(5, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    CompletableFuture<Void> emitAsync(LibraryEvent libraryEvent) {
        Integer key = 42;
        String value = libraryEvent.toString();

        return kafkaTemplate.send(topic, key, value)
                .thenAccept(sendResult -> { });
    }
}
