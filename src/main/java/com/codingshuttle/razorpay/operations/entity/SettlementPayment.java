package com.codingshuttle.razorpay.operations.entity;

import com.codingshuttle.razorpay.payment.entity.Payment;
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

    @MapsId("settlementId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "settlement_id", nullable = false)
    private Settlement settlement;

    @MapsId("paymentId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

}
