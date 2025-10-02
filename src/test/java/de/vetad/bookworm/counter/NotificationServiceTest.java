package de.vetad.bookworm.counter;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

class NotificationServiceTest {

    static ConfluentKafkaContainer kafka;

    @BeforeAll
    static void beforeAll() {
        DockerImageName kafkaImageName = DockerImageName
                .parse("artifactory.datev.de/docker-mirror/confluentinc/cp-kafka:7.7.5")
                .asCompatibleSubstituteFor("confluentinc/cp-kafka");

            kafka = new ConfluentKafkaContainer(kafkaImageName);
            kafka.start();
    }

    @AfterAll
    static void afterAll() {
        kafka.stop();
    }

    @Test
    @DisplayName("Should produce a library event")
    void shouldProduceLibraryEvent() {
        Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);
        LOGGER.info("there will be tests, soon ...");
    }
}