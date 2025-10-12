package de.vetad.bookworm.counter;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.version}")
public class LibraryEventsController {

    private final NotificationService notificationService;

    public LibraryEventsController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/library/event")
    public ResponseEntity<LibraryEvent> libraryEvent(
            @RequestBody LibraryEvent libraryEvent
    ) {
        notificationService.emitBlocking(libraryEvent);

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(libraryEvent);
    }
}
