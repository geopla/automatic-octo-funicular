# Bookworm
Sample project to fiddle around with Spring Boot Kafka.

## Preconditions
- Set environment variable `IMAGE_PROXY` if there is a proxy needed to pull an image

## Counter
- Scanning books and produce Kafka events
- see [HTTP requests](doc/requests.http) for sending commands

## Shelf
Locate a shelf for a book; consumes Kafka events