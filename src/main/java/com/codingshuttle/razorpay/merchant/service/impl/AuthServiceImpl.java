package com.codingshuttle.razorpay.merchant.service.impl;

import com.codingshuttle.razorpay.merchant.dto.request.MerchantSignupRequest;
import com.codingshuttle.razorpay.merchant.dto.response.MerchantResponse;
import com.codingshuttle.razorpay.merchant.repository.AppUserRepository;
import com.codingshuttle.razorpay.merchant.repository.MerchantRepository;
import com.codingshuttle.razorpay.merchant.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final MerchantRepository merchantRepository;
    private final AppUserRepository appUserRepository;

    @Override
    public @Nullable MerchantResponse signup(final MerchantSignupRequest merchantSignupRequest) {
        if (merchantRepository.existsByEmail(merchantSignupRequest.email())) {
            throw new RuntimeException(String.format("Email %s already exists", merchantSignupRequest.email()));
        }
        return null;
    }
}
