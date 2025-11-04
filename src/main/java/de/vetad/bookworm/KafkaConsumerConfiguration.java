package de.vetad.bookworm;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConsumerConfiguration {

//    @Bean
//    public NewTopic inventoryEventTopic(@Value("${notifications.libraryevent.topic}") String topic) {
//        return TopicBuilder.name(topic)
//                .partitions(3)
//                .replicas(3)
//                .build();
//    }
}
