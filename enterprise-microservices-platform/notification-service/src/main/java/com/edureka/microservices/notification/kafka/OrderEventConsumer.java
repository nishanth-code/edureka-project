package com.edureka.microservices.notification.kafka;

import com.edureka.microservices.notification.event.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(OrderEventConsumer.class);

    @KafkaListener(topics = "order-events", groupId = "notification-service-group")
    public void consumeOrderCreatedEvent(OrderCreatedEvent event) {
        logger.info("Received OrderCreatedEvent: orderId={}, userId={}, productId={}, status={}",
            event.orderId(), event.userId(), event.productId(), event.status());

        try {
            sendNotification(event);
            logger.info("Notification sent successfully for order: {}", event.orderId());
        } catch (Exception ex) {
            logger.error("Failed to send notification for order: {}, error: {}", event.orderId(), ex.getMessage());
        }
    }

    private void sendNotification(OrderCreatedEvent event) {
        String message = String.format(
            "Order Notification: Order #%d has been %s. " +
            "Product ID: %d, Quantity: %d, User ID: %d, Created: %s",
            event.orderId(), event.status(), event.productId(),
            event.quantity(), event.userId(), event.createdAt()
        );
        logger.info("NOTIFICATION: {}", message);
    }

}
