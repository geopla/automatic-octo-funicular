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

    @PostMapping("/library/event")
    public ResponseEntity<LibraryEvent> libraryEvent(
            @RequestBody LibraryEvent libraryEvent
    ) {

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(libraryEvent);
    }
}
