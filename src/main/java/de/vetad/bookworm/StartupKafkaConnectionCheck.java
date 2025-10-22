package de.vetad.bookworm;

import ch.qos.logback.classic.Level;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.common.errors.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

@Profile("with-kafka-connectivity-check")
@Component
public class StartupKafkaConnectionCheck implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartupKafkaConnectionCheck.class);
    private static final int EXIT_CODE = 1;

    private final ApplicationContext applicationContext;
    private final KafkaProperties kafkaProperties;

    private Map<ch.qos.logback.classic.Logger, Level> adminClientLoggers = new HashMap<>();

    public StartupKafkaConnectionCheck(ApplicationContext applicationContext, KafkaProperties kafkaProperties) {
        this.applicationContext = applicationContext;
        this.kafkaProperties = kafkaProperties;

        initAdminClientLoggers();
    }

    @Override
    public void run(ApplicationArguments args) throws ExecutionException, InterruptedException {
        boolean isReachable = connectionCheckWithReducedLogs(this::isKafkaBrokerReachable);

        if (! isReachable) {
            LOGGER.error("failed to reach Kafka broker, shutting down - did you start Kafka at all?");
            shutdownApplicationContext();

            // is that really a good idea?
            shutdownJVM();
        }
    }

    boolean isKafkaBrokerReachable()  {
        var adminProperties = kafkaProperties.buildAdminProperties(null);

        try (AdminClient adminClient = AdminClient.create(adminProperties)) {
            try {
                adminClient.listTopics(new ListTopicsOptions().timeoutMs(1000))
                        .names()
                        .get();

                // gotcha
                return true;
            }
            catch (Exception e) {
                if (e.getCause() instanceof TimeoutException) {
                    // Kafka broker not reachable within given time
                    return false;
                }
                else {
                    LOGGER.error("something went wrong while checking Kafka broker connectivity, %s".formatted(e.getMessage()));
                    throw new RuntimeException(e);
                }
            }
        }
    }

    boolean connectionCheckWithReducedLogs(Supplier<Boolean> kafkaBrokerReachable) throws ExecutionException, InterruptedException {
        setAdminClientLoggersLevel(Level.ERROR);

        try {
            return CompletableFuture.supplyAsync(kafkaBrokerReachable).get();
        } finally {
            restoreAdminClientLoggersLevel();
        }
    }

    void initAdminClientLoggers() {
        // for getting rid of:
        //   [AdminClient clientId=adminclient-1] Timed out 1 remaining operation(s) during close.
        adminClientLoggers.put(
                (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("org.apache.kafka.clients.admin.KafkaAdminClient"),
                ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger("org.apache.kafka.clients.admin.KafkaAdminClient")).getLevel()
        );
        // for getting rid of:
        //   'AdminClientConfig values'
        adminClientLoggers.put(
                (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("org.apache.kafka.clients.admin.AdminClientConfig"),
                ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger("org.apache.kafka.clients.admin.AdminClientConfig")).getLevel()
        );
        // for getting rid of:
        //   [AdminClient clientId=adminclient-1] Node -1 disconnected.
        //   [AdminClient clientId=adminclient-1] Connection to node -1 (localhost/127.0.0.1:9092) could not be established. Node may not be available.
        adminClientLoggers.put(
                (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("org.apache.kafka.clients.NetworkClient"),
                ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger("org.apache.kafka.clients.NetworkClient")).getLevel()
        );
        // for getting rid of:
        //   Metrics scheduler closed
        //   ...
        adminClientLoggers.put(
                (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("org.apache.kafka.common.metrics.Metrics"),
                ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger("org.apache.kafka.common.metrics.Metrics")).getLevel()
        );
    }

    void setAdminClientLoggersLevel(Level level) {
        adminClientLoggers.keySet().forEach(logger -> logger.setLevel(level));
    }

    void restoreAdminClientLoggersLevel() {
        adminClientLoggers.forEach(ch.qos.logback.classic.Logger::setLevel);
    }

    void shutdownApplicationContext() {
        ExitCodeGenerator exitCodeGenerator =  () -> EXIT_CODE;
        SpringApplication.exit(applicationContext, exitCodeGenerator);
    }

    void shutdownJVM() {
        System.exit(EXIT_CODE);
    }
}
