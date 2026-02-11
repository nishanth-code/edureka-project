package com.edureka.microservices.auth.entity;

public enum Role {
    USER("ROLE_USER", "Standard user"),
    ADMIN("ROLE_ADMIN", "Administrator"),
    SUPPORT("ROLE_SUPPORT", "Support staff");

    private final String authority;
    private final String description;

    Role(String authority, String description) {
        this.authority = authority;
        this.description = description;
    }

    public String getAuthority() {
        return authority;
    }

    public String getDescription() {
        return description;
    }

}
