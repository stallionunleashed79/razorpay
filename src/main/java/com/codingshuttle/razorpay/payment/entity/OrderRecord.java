package com.codingshuttle.razorpay.payment.entity;

import com.codingshuttle.razorpay.common.entity.Money;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "order_record")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    //No FK due to cross service boundary
    @Column(name="merchant_id", nullable = false)
    private UUID merchantId;

    @Embedded
    private Money amount;
}
