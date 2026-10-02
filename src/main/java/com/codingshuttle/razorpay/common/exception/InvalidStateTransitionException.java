package com.codingshuttle.razorpay.common.exception;

import lombok.Getter;

@Getter
public class InvalidStateTransitionException extends RuntimeException {

    private final String paymentStatus;
    private final String paymentEvent;

    public InvalidStateTransitionException(final String paymentStatus, final String paymentEvent) {
        super("Invalid transition from: " + paymentStatus + " with event " + paymentEvent);
        this.paymentStatus = paymentStatus;
        this.paymentEvent = paymentEvent;
    }
}
