package com.codingshuttle.razorpay.merchant.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "customer")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Customer extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "marchant_id", nullable = false)
    private Merchant merchant;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 200)
    private String email;

    @Column(length = 20)
    private String contactNumber;

    private LocalDateTime deletedAt;
}
