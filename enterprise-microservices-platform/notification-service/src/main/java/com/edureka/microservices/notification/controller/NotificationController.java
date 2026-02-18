package com.edureka.microservices.notification.controller;

import com.edureka.microservices.notification.event.OrderCreatedEvent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "Notification management endpoints")
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    @PostMapping("/send")
    @Operation(summary = "Send order notification")
    public ResponseEntity<Void> sendNotification(@RequestBody OrderCreatedEvent event) {
        logger.info("Received OrderCreatedEvent: orderId={}, userId={}, productId={}, status={}",
            event.orderId(), event.userId(), event.productId(), event.status());

        try {
            sendNotificationInternal(event);
            logger.info("Notification sent successfully for order: {}", event.orderId());
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception ex) {
            logger.error("Failed to send notification for order: {}, error: {}", event.orderId(), ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private void sendNotificationInternal(OrderCreatedEvent event) {
        String message = String.format(
            "Order Notification: Order #%d has been %s. " +
            "Product ID: %d, Quantity: %d, User ID: %d, Created: %s",
            event.orderId(), event.status(), event.productId(),
            event.quantity(), event.userId(), event.createdAt()
        );
        logger.info("NOTIFICATION: {}", message);
    }
}
