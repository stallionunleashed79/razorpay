package com.codingshuttle.razorpay.payment.processor.strategy;

import com.codingshuttle.razorpay.common.util.RandomizerUtil;
import com.codingshuttle.razorpay.payment.processor.PaymentProcessor;
import com.codingshuttle.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.codingshuttle.razorpay.payment.processor.dto.PaymentProcessorResponse;

public class UpiPaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {
        final String VPA_CODE_FAIL = "fail@okaxis";
        final String UPI_REJECTED = "UPI_REJECTED";
        final String bankCode = request.paymentDetails() != null
                ? request.paymentDetails().get("vpa").toString()
                : null;
        if (VPA_CODE_FAIL.equals(bankCode)) {
            return new PaymentProcessorResponse.Failure(UPI_REJECTED,
                    "Bank rejected the transaction registration");
        }
        final String processorRef = String.format("UPI_PROCESSOR: %s",
                RandomizerUtil.randomBase64(16));
        final String bankRef = String.format("BANK_REF%s", RandomizerUtil.randomBase64(16));
        return new PaymentProcessorResponse.Success(processorRef, bankRef);
    }
}
