package de.vetad.bookworm.counter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);

    void emit(LibraryEvent libraryEvent) {

        // TODO NotificationService writes to a Testcontainers Kafka broker

    }
}
