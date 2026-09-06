package com.codingshuttle.razorpay.payment.controller;

import com.codingshuttle.razorpay.payment.dto.request.PaymentInitRequest;
import com.codingshuttle.razorpay.payment.dto.response.PaymentResponse;
import com.codingshuttle.razorpay.payment.service.PaymentService;
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
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    UUID merchantId = UUID.fromString("499dc8d8-e552-4782-a707-8b2bbf80b9b5");

    @PostMapping
    public ResponseEntity<PaymentResponse> initiatePayment(@Valid @RequestBody PaymentInitRequest paymentInitRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                paymentService.initiatePayment(merchantId, paymentInitRequest));
    }
}
