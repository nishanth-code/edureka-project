package com.edureka.microservices.order.kafka;

import com.edureka.microservices.order.event.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(OrderEventProducer.class);
    private static final String TOPIC = "order-events";

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public OrderEventProducer(KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishOrderCreatedEvent(OrderCreatedEvent event) {
        logger.info("Publishing OrderCreatedEvent for order ID: {}", event.orderId());
        kafkaTemplate.send(TOPIC, event.orderId().toString(), event);
        logger.info("OrderCreatedEvent published successfully for order ID: {}", event.orderId());
    }

}
