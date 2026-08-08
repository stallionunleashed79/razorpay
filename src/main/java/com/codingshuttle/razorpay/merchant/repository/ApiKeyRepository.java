package com.codingshuttle.razorpay.merchant.repository;

import com.codingshuttle.razorpay.merchant.entity.ApiKey;
import com.codingshuttle.razorpay.merchant.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {

    // Fetch by an actual Merchant entity object
    List<ApiKey> findByMerchant(Merchant merchant);
}
