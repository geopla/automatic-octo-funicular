package de.vetad.bookworm.counter;

import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;


@WebMvcTest(LibraryEventsController.class)
@PropertySource("classpath:application.properties")
class LibraryEventsControllerTest {

    @Value("${api.version}")
    String apiVersion;

    @Autowired
    WebTestClient client;

    @Test
    @DisplayName("Should accept a library event")
    void shouldAcceptLibraryEvent() {
        String path = "/%s/library/event".formatted(apiVersion);

        var libraryEvent = Instancio.of(LibraryEvent.class).create();

        client.post()
                .uri(path)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(libraryEvent)
                .exchange()
                .expectStatus().isAccepted();
    }
}