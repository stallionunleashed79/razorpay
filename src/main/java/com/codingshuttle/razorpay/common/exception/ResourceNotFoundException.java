package com.codingshuttle.razorpay.common.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    private final String resourceName;
    private final Object resourceIdentifier;

    public ResourceNotFoundException(String resourceName, Object resourceIdentifier) {
        super(String.format("Resource %s not found", resourceName));
        this.resourceName = resourceName;
        this.resourceIdentifier = resourceIdentifier;
    }
}
