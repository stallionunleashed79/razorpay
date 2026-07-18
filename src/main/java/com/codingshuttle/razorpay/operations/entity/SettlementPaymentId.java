package com.codingshuttle.razorpay.operations.entity;

import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
class SettlementPaymentId {

    private UUID settlementId;

    private UUID paymentId;
}
