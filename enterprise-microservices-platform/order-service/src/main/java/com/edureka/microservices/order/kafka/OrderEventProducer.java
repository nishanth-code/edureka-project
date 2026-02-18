package com.edureka.microservices.order.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @deprecated Kafka has been replaced with synchronous REST calls via NotificationServiceClient.
 * The order-service now uses Feign client to directly call notification-service.
 * This class is no longer used and can be deleted.
 */
@Deprecated
@Component
public class OrderEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(OrderEventProducer.class);

    public OrderEventProducer() {
        logger.warn("OrderEventProducer is deprecated. Use NotificationServiceClient instead.");
    }

    @Deprecated
    public void publishOrderCreatedEvent(Object event) {
        // This method is no longer used
        logger.warn("publishOrderCreatedEvent is deprecated. Use NotificationServiceClient instead.");
    }
}
