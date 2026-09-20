package com.codingshuttle.razorpay.payment.gateway.adapter.impl;

import com.codingshuttle.razorpay.common.enums.PaymentMethod;
import com.codingshuttle.razorpay.payment.gateway.adapter.PaymentAdapter;
import com.codingshuttle.razorpay.payment.gateway.adapter.factory.PaymentProcessorRouter;
import com.codingshuttle.razorpay.payment.gateway.dto.PaymentRequest;
import com.codingshuttle.razorpay.payment.gateway.dto.PaymentResult;
import com.codingshuttle.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.codingshuttle.razorpay.payment.processor.dto.PaymentProcessorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class NetBankingAdapter implements PaymentAdapter {

    private final PaymentProcessorRouter paymentProcessorRouter;

    @Override
    public PaymentResult initiatePayment(PaymentRequest paymentRequest) {
        log.info("Initiating net banking payment for payment Id: {}", paymentRequest.paymentId());
        try {
            final PaymentProcessorRequest paymentProcessorRequest = PaymentProcessorRequest.nonCard(
                    paymentRequest.paymentId(),
                    PaymentMethod.NETBANKING,
                    paymentRequest.amount(), paymentRequest.methodDetails());
            final PaymentProcessorResponse paymentProcessorResponse = paymentProcessorRouter.charge(
                    paymentProcessorRequest);

            return switch (paymentProcessorResponse) {
                case PaymentProcessorResponse.Failure failure -> new PaymentResult.Failure(
                        failure.errorCode(), failure.errorDescription());
                case PaymentProcessorResponse.Pending pending -> new PaymentResult.Pending(
                        pending.processorReference());
                case PaymentProcessorResponse.Success success -> new PaymentResult.Success(
                        success.bankReference());
            };
        } catch (Exception e) {
            log.error("Error initiating net banking payment for payment Id: {}", paymentRequest.paymentId(), e);
            return new PaymentResult.Failure("NETBANKING_INIT_ERROR", "Error initiating net banking payment.");
        }
    }

}
