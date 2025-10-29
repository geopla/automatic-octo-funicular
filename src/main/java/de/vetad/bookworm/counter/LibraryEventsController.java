package de.vetad.bookworm.counter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.version}")
public class LibraryEventsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LibraryEventsController.class);

    private final NotificationService notificationService;

    public LibraryEventsController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/library/event")
    public ResponseEntity<LibraryEvent> libraryEvent(
            @RequestBody LibraryEvent libraryEvent
    ) {
        SendResult<Integer, LibraryEvent> sendResult = notificationService.emitBlocking(libraryEvent);

        LOGGER.info("gotta send result: {}", sendResult);

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(libraryEvent);
    }
}
