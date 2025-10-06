package de.vetad.bookworm.logging;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class LogConfigurationForUnitTest {

    static final Logger LOGGER = LoggerFactory.getLogger(LogConfigurationForUnitTest.class);

    @Test
    @DisplayName("Should have INFO enabled by default for simple unit test")
    void shouldLoadLogConfiguration() {

        // not a bullet proofed test for the absence of logback.xml or logback-test.xml
        assertThat(LOGGER.isInfoEnabled()).isTrue();
        assertThat(LOGGER.isDebugEnabled()).isFalse();
    }
}
