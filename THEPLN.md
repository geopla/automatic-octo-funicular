# The PLN

## Learnings
- no need to start a Spring Boot application context when testing a 'Kafka' service
- reading from a topic with Kafka test utils is not much of a win
- no `@Configuration` class for Kafka needed at all when using Spring autoconfiguration

### Configuration
**TODO**  
- how to organize Kafka configuration for applications and tests? 

### Testcontainers Kafka Broker in plain unit test
- start container image as in [NotificationServiceTes](./src/test/java/de/vetad/bookworm/counter/NotificationServiceTest.java)
- using the same topic in different test methods may not work as intended
  - create new topic for each test?
  - prevent parallel test execution?
- shut down the container gracefully

### Embedded Kafka Broker in plain unit test
- requires `spring-test-kafka` (3.3.x)
- `EmbeddedKafkaZBroker` is default, `@EmbeddedKafka(kraft = true)` gets you the `EmbeddedKafkaKraftBroker` instead

### Testcontainers Kafka Broker in integration test
**TODO**

### Embedded Kafka Broker in integration test
**TODO**

### Questions
- The broker is configured to create new topics automatically. Can we create our own topic specifying the number of partitions?
- Is it possible to use autoconfiguration for Kafka tests despite having a configuration class in `src/main/java`?

## Troubleshooting
### Kafka startup warnings
When running the application local and sending a request the following happens
```
2025-10-21T09:53:18.757+02:00  INFO 29656 --- [bookworm] [nio-8080-exec-1] o.a.k.clients.producer.ProducerConfig    : ProducerConfig values:
...
bootstrap.servers = [localhost:9092]
client.dns.lookup = use_all_dns_ips
client.id = bookworm-producer-1
key.serializer = class org.apache.kafka.common.serialization.IntegerSerializer
security.protocol = PLAINTEXT
value.serializer = class org.springframework.kafka.support.serializer.JsonSerializer
...
 o.a.k.c.t.i.KafkaMetricsCollector        : initializing Kafka metrics collector
 o.a.k.clients.producer.KafkaProducer     : [Producer clientId=bookworm-producer-1] Instantiated an idempotent producer.
 o.a.kafka.common.utils.AppInfoParser     : Kafka version: 3.9.1
 o.a.kafka.common.utils.AppInfoParser     : Kafka commitId: f745dfdcee2b9851
 o.a.kafka.common.utils.AppInfoParser     : Kafka startTimeMs: 1761033198831
 org.apache.kafka.clients.NetworkClient   : [Producer clientId=bookworm-producer-1] Node -1 disconnected.
 org.apache.kafka.clients.NetworkClient   : [Producer clientId=bookworm-producer-1] Connection to node -1 (localhost/127.0.0.1:9092) could not be established. Node may not be available.
 org.apache.kafka.clients.NetworkClient   : [Producer clientId=bookworm-producer-1] Bootstrap broker localhost:9092 (id: -1 rack: null) disconnected
```


## The Course
...