package de.vetad.bookworm;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class BookwormApplicationTest {

    @Container
    static final ConfluentKafkaContainer kafkaContainer = new ConfluentKafkaContainer(
            DockerImageName.parse("%s%s".formatted(
                            System.getenv().getOrDefault("IMAGE_PROXY", ""),
                            "confluentinc/cp-kafka:7.7.5"))
                    .asCompatibleSubstituteFor("confluentinc/cp-kafka")
    );

    @Test
    void contextLoads(ApplicationContext context) {
        assertThat(context).isNotNull();
    }

}
