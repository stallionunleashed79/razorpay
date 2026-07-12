package com.codingshuttle.razorpay.vault.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "vault_card")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VaultCard {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length=4)
    private String lastFour;

    @Column(nullable = false, length=6)
    private String bin;

    @Column(nullable = false)
    private byte[] encryptedPan;

    @Column(nullable = false)
    private byte[] encryptedDek;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private Integer expiryMonth;

    @Column(nullable = false)
    private Integer expiryYear;

    @Column(nullable = false)
    private String cardHolderName;

    private LocalDateTime deletedAt;
}
