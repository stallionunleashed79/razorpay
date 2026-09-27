package com.codingshuttle.razorpay.payment.gateway.adapter;

import com.codingshuttle.razorpay.common.enums.PaymentMethod;
import com.codingshuttle.razorpay.payment.gateway.adapter.factory.PaymentProcessorRouter;
import com.codingshuttle.razorpay.payment.gateway.dto.PaymentRequest;

import com.codingshuttle.razorpay.payment.gateway.dto.PaymentResult;
import com.codingshuttle.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.codingshuttle.razorpay.payment.processor.dto.PaymentProcessorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public abstract class PaymentAdapter {

    protected final PaymentProcessorRouter paymentProcessorRouter;
    protected abstract PaymentMethod getPaymentMethod();

    // The single, unified logic flow shared by ALL subclasses
    public PaymentResult initiatePayment(PaymentRequest paymentRequest) {
        final PaymentMethod paymentMethod = getPaymentMethod();
        log.info("Initiating {} payment for payment Id: {}", paymentMethod, paymentRequest.paymentId());
        try {
            final PaymentProcessorRequest paymentProcessorRequest = PaymentProcessorRequest.nonCard(
                    paymentRequest.paymentId(),
                    paymentMethod,
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

    public PaymentResult capture(UUID paymentId) {
        return new PaymentResult.Success(String.format("%s_REF", getPaymentMethod().name()));
    }
}

