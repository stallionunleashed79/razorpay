package com.codingshuttle.razorpay.common.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    private final String resourceName;
    private final String resourceIdentifier;

    public ResourceNotFoundException(String resourceName, String resourceIdentifier, String message) {
        super(message);
        this.resourceName = resourceName;
        this.resourceIdentifier = resourceIdentifier;
    }
}
