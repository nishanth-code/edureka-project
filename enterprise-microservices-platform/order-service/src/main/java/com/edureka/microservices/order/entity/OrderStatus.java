package com.edureka.microservices.order.entity;

public enum OrderStatus {
    PENDING("Pending", "Order is pending processing"),
    CONFIRMED("Confirmed", "Order has been confirmed"),
    SHIPPED("Shipped", "Order has been shipped"),
    DELIVERED("Delivered", "Order has been delivered"),
    CANCELLED("Cancelled", "Order has been cancelled");

    private final String displayName;
    private final String description;

    OrderStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

}
