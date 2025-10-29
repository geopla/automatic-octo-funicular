package de.vetad.bookworm.shelf;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class InventoryEventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryEventConsumer.class);

    @KafkaListener(
            topics = "${notifications.libraryevent.topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            concurrency = "${spring.kafka.listener.concurrency}"
    )
    public void listen(ConsumerRecord<Integer, InventoryEvent> consumerRecord) {
        LOGGER.info("---> Received key={} value={} partition={} offset={}",
                consumerRecord.key(),
                consumerRecord.value(),
                consumerRecord.partition(),
                consumerRecord.offset());
    }
}
