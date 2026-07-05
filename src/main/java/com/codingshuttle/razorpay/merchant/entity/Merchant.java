package com.codingshuttle.razorpay.merchant.entity;

import com.codingshuttle.razorpay.common.enums.BusinessType;
import com.codingshuttle.razorpay.common.enums.MerchantStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "merchant")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Merchant extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(length = 20)
    private String contactNumber;

    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    private BusinessType businessType;

    @Column(length = 50)
    private String businessName;

    @Column(length = 200)
    private String websiteUrl;

    @Column(nullable = false, length = 20)
    private MerchantStatus status = MerchantStatus.PENDING_KYC;

    @OneToMany(mappedBy = "merchant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AppUser> employees = new ArrayList<>();

    @Column(length = 20)
    private String gstId;

    @Column(length = 20)
    private String panId;
    private String settlementBankAccount;

    @Column(length = 20)
    private String settlementBankIfsc;

    @Column(length = 200)
    private String settlementBankAccountHolderName;
}
