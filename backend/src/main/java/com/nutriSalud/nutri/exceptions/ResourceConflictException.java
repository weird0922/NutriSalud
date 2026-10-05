package com.nutriSalud.nutri.exceptions;

public class ResourceConflictException extends RuntimeException {
    private final String resourceName;
    private final String reason;

    public ResourceConflictException(String resourceName, String reason) {
        super(String.format("Conflicto en %s: %s", resourceName, reason));
        this.resourceName = resourceName;
        this.reason = reason;
    }

    public String getResourceName() { return resourceName; }
    public String getReason() { return reason; }
}
