package com.codingshuttle.razorpay.vault.controller;

import com.codingshuttle.razorpay.vault.dto.TokenizeRequest;
import com.codingshuttle.razorpay.vault.dto.TokenizeResponse;
import com.codingshuttle.razorpay.vault.service.VaultService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/vault")
public class VaultController {

    private final VaultService vaultService;

    UUID merchantId = UUID.fromString("499dc8d8-e552-4782-a707-8b2bbf80b9b5"); //TODO REPLACE WITH MERCHANTCONTEXT

    @PostMapping("/tokenize")
    public ResponseEntity<TokenizeResponse> tokenize(@RequestBody @Valid TokenizeRequest tokenizeRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(vaultService.tokenize(tokenizeRequest));
    }
}
