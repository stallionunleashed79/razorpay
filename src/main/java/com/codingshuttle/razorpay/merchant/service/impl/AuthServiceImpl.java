package com.codingshuttle.razorpay.merchant.service.impl;

import com.codingshuttle.razorpay.common.enums.MerchantStatus;
import com.codingshuttle.razorpay.common.enums.UserRole;
import com.codingshuttle.razorpay.common.exception.DuplicateResourceException;
import com.codingshuttle.razorpay.merchant.dto.request.MerchantSignupRequest;
import com.codingshuttle.razorpay.merchant.dto.response.MerchantResponse;
import com.codingshuttle.razorpay.merchant.entity.AppUser;
import com.codingshuttle.razorpay.merchant.entity.Merchant;
import com.codingshuttle.razorpay.merchant.repository.AppUserRepository;
import com.codingshuttle.razorpay.merchant.repository.MerchantRepository;
import com.codingshuttle.razorpay.merchant.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final MerchantRepository merchantRepository;
    private final AppUserRepository appUserRepository;

    @Override
    @Transactional
    public @Nullable MerchantResponse signup(final MerchantSignupRequest merchantSignupRequest) {
        if (merchantRepository.existsByEmail(merchantSignupRequest.email())) {
            throw new DuplicateResourceException("DUPLICATE_MERCHANT", String.format("Email %s already exists", merchantSignupRequest.email()));
        }

        Merchant merchant = Merchant.builder()
                .name(merchantSignupRequest.name())
                .email(merchantSignupRequest.email())
                .businessName(merchantSignupRequest.businessName())
                .businessType(merchantSignupRequest.businessType())
                .status(MerchantStatus.PENDING_KYC)
                .build();
        merchant = merchantRepository.save(merchant);
        final AppUser appUser = AppUser.builder()
                .email(merchantSignupRequest.email())
                .passwordHash(merchantSignupRequest.password())
                .role(UserRole.OWNER)
                .merchant(merchant)
                .build();
        appUserRepository.save(appUser);
        return MerchantResponse.builder()
                .id(merchant.getId())
                .name(merchant.getName())
                .email(merchant.getEmail())
                .businessName(merchant.getBusinessName())
                .businessType(merchant.getBusinessType())
                .merchantStatus(merchant.getStatus())
                .build();
    }
}
