package de.vetad.bookworm.logging;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class LogConfigurationForSpringTest {

    static final Logger LOGGER = LoggerFactory.getLogger(LogConfigurationForSpringTest.class);

    @Test
    @DisplayName("Should use logback-spring.xml for Spring Boot test")
    void shouldLoadLogbackSpring() {

        // can't test that logback-spring.xml is processed but check for its side effects
        assertThat(LOGGER.isTraceEnabled()).isTrue();
    }
}
