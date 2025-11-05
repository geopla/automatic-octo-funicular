package de.vetad.bookworm.counter;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaConnectionDetails;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTestWithContainers
public class LibraryEventsControllerIT {

    @Autowired
    KafkaConnectionDetails connectionDetails;

    @Test
    @DisplayName("Should work at all")
    void shouldWorkAtAll() {
        assertThat(connectionDetails).isNotNull();
    }
}
