# The PLN

## Learnings
- no need to start a Spring Boot application context when testing a 'Kafka' service
- reading from a topic with Kafka test utils is not much of a win

### Configuration
- No `@Configuration` class for Kafka needed when using Spring autoconfiguration

### Testcontainers Kafka Broker in plain unit test
- start container image as in [NotificationServiceTes](./src/test/java/de/vetad/bookworm/counter/NotificationServiceTest.java)
- using the same topic in different test methods may not work as intended
  - create new topic for each test?
  - prevent parallel test execution?
- shut down the container gracefully

### Embedded Kafka Broker in plain unit test
- requires `spring-test-kafka` (3.3.x)
- `EmbeddedKafkaZBroker` is default, `@EmbeddedKafka(kraft = true)` gets you the `EmbeddedKafkaKraftBroker` instead

### Questions
- The broker is configured to create new topics automatically. Can we create our own topic specifying the number of partitions?
- Is it possible to use autoconfiguration for Kafka tests despite having a configuration class in `src/main/java`?

## The Course
...