package com.codingshuttle.razorpay.operations.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;

import java.util.UUID;

@Embeddable
class SettlementPaymentId {

    @Embedded
    private UUID settlementId;

    @Embedded
    private UUID paymentId;

}
