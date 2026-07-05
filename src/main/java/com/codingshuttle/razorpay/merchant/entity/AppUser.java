package com.codingshuttle.razorpay.merchant.entity;

import com.codingshuttle.razorpay.common.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "app_user")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppUser extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "marchant_id", nullable = false)
    private Merchant merchant;

    private String email;
    private String passwordHash;
    private UserRole role;
}
