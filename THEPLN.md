# The PLN

## The Basics
- [x] run Kafka in a container using a unit test
- [ ] produce and consume a Kafka message from a unit test
  - [x] configure `KafkaTemplate` in a unit test
  - [x] produce a message with body type `String` using `KafkaTemplate`, check the broker that the message has been written to a partition
    - [x] switch to asynchronous message sending
  - [x] consume a message using `KafkaConsumer`, completing the test
  - [x] change the message body to `LibraryEvent`
- [ ] consume a message using the `KafkaListener` - may be evaluating the annotations too?
  - [ ] produce a message with `KafkaProducer`
- [ ] produce and consume a Kafka message from a `@SpringBootTest`
  - [ ] create required `KafkaConfiguration`
  - [ ] produce message
  - [ ] consume message with listener

### Questions
- The broker is configured to create new topics automatically. Can we create our own topic specifying the number of partitions?

## The Course
...