# Bookworm
Sample project to fiddle around with Spring Boot Kafka.

## Preconditions
- set environment variable `IMAGE_PROXY` if there is a proxy needed to pull an image
- remember to start Podman Desktop

## Running the application locally
- start the container with `podman compose up --detach`
  - check Podman Desktop if container is actually running
  - make sure a `.env` file with variable `IMAGE_PROXY` is present in case a proxy is required to pull the image

## Counter
- scanning books and produce Kafka events
- see [HTTP requests](doc/requests.http) for sending commands

## Shelf
Locate a shelf for a book; consumes Kafka events

## Learnings

### Logging configuration
Use `logback-spring.xml` as the only Logback configuration. No other files. Unit tests will
use the Logback defaults. If necessary use system property `logback.configurationFile` to run
a unit test with a dedicated configuration. Another option is to cast the SLF4J logger to
`ch.qos.logback.classic.Logger` and reconfigure the logging through that API. It is not possible
to have separate Logback configurations for unit and integration tests.

## Modulith
Cool, but got no time to investigate that topic in the projects context more thoroughly.
Not sure how it may fit into event streaming with Kafka other than getting prepared to
the change for splitting up into microservices. Somebody please enlighten me.