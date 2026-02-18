package com.edureka.microservices.order.client;

import com.edureka.microservices.order.event.OrderCreatedEvent;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service", url = "${feign.client.config.notification-service.url:http://localhost:8086}")
public interface NotificationServiceClient {

    @PostMapping("/api/notifications/send")
    void sendNotification(@RequestBody OrderCreatedEvent event);
}
