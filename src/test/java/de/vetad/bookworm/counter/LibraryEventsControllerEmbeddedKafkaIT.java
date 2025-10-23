package de.vetad.bookworm.counter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaConnectionDetails;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EmbeddedKafka(kraft = true)
public class LibraryEventsControllerEmbeddedKafkaIT {

    @Autowired
    KafkaConnectionDetails connectionDetails;

    @Test
    @DisplayName("Should work at all")
    void shouldWorkAtAll() {
        assertThat(connectionDetails).isNotNull();
    }
}
