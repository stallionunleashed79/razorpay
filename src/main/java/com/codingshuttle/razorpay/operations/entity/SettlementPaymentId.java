package com.codingshuttle.razorpay.operations.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettlementPaymentId implements Serializable {

    private UUID settlementId;

    private UUID paymentId;
}
