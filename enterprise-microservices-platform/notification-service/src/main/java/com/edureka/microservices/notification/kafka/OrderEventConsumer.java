package com.edureka.microservices.notification.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @deprecated Kafka has been replaced with synchronous REST calls.
 * The notification-service now has a REST controller to receive order notifications directly.
 * This class is no longer used and can be deleted.
 */
@Deprecated
@Component
public class OrderEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(OrderEventConsumer.class);

    public OrderEventConsumer() {
        logger.warn("OrderEventConsumer is deprecated. Use NotificationController REST endpoint instead.");
    }

    @Deprecated
    public void consumeOrderCreatedEvent(Object event) {
        // This method is no longer used
        logger.warn("consumeOrderCreatedEvent is deprecated. Use NotificationController REST endpoint instead.");
    }
}
