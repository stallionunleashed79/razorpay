package com.codingshuttle.razorpay.payment.entity;


import com.codingshuttle.razorpay.common.enums.PaymentStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

public class PaymentTransitionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "event", length = 30)
    private PaymentEvent event;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "from_status", length = 30)
    private PaymentStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "to_status", length = 30)
    private PaymentStatus toStatus;

    @Column(name = "actor", nullable = false, length = 30)
    private String actor;

    @Column(name = "occured_at", nullable = false)
    private LocalDateTime occuredAt;
}
