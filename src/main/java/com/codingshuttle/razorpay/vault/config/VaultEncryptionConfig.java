package com.codingshuttle.razorpay.vault.config;

import com.codingshuttle.razorpay.vault.util.VaultUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.BytesEncryptor;

import java.util.Base64;

@Configuration
public class VaultEncryptionConfig {

    @Value("${vault.encryption.master-key}")
    private String masterKey;


    @Bean
    public BytesEncryptor decrypter() {
        final byte[] masterKeyBytes = Base64.getDecoder().decode(masterKey);
        return VaultUtils.panEncryptor(masterKeyBytes);
    }
}
