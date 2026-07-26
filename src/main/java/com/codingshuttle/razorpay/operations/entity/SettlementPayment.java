package com.codingshuttle.razorpay.operations.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "settlement_payment")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SettlementPayment {

    @EmbeddedId
    private SettlementPaymentId settlementPaymentId;

    @MapsId()
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "settlement_id", nullable = false)
    private Settlement settlement;

}
