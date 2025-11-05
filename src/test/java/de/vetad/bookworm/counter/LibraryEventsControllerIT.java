package de.vetad.bookworm.counter;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTestWithContainers
public class LibraryEventsControllerIT {

    @Value("${api.version}")
    String apiVersion;

    WebTestClient client;

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        client = WebTestClient.bindToServer()
                .baseUrl("http://localhost:%d".formatted(port))
                .build();
    }

    @Test
    @DisplayName("Should process a library event")
    void shouldProcessLibraryEvent() {
        String path = "/%s/library/event".formatted(apiVersion);

        var libraryEvent = new LibraryEvent(
                42,
                LibraryEventType.NEW,
                new Book(
                        4422,
                        "Guards! Guards!",
                        "Terry Pratchatt"
                )
        );

        client.post()
                .uri(path)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(libraryEvent)
                .exchange()
                .expectStatus().isAccepted();

        // TODO check the database - tricky because of asynchronous processing

    }
}
