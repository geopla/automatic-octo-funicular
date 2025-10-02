package de.vetad.bookworm.counter;


public record LibraryEvent(
        Integer id,
        LibraryEventType type,
        Book book
) {
}
