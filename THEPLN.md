# The PLN

## The Basics
- [x] run Kafka in a container using a unit test
- [ ] produce and consume a Kafka message from a unit test
  - [x] configure `KafkaTemplate` in a unit test
  - [x] produce a message with body type `String`, check the broker that the message has been written to a partition
  - [ ] consume a message, completing the test
  - [ ] change the message body to `LibraryEvent`
- [ ] produce and consume a Kafka message from a `@SpringBootTest`
  - [ ] create required `KafkaConfiguration`
  - [ ] produce message
  - [ ] consume message with listener

### Questions
- The broker is configured to create new topics automatically. Can we create our own topic specifying the number of partitions?

## The Course
...