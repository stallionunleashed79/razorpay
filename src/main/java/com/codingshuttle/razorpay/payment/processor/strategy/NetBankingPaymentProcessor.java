package com.codingshuttle.razorpay.payment.processor.strategy;

import com.codingshuttle.razorpay.common.util.RandomizerUtil;
import com.codingshuttle.razorpay.payment.processor.PaymentProcessor;
import com.codingshuttle.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.codingshuttle.razorpay.payment.processor.dto.PaymentProcessorResponse;

public class NetBankingPaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {
        final String BANK_CODE_FAIL = "BANK_CODE_FAIL";
        final String BANK_REJECTED = "BANK_REJECTED";
        final String bankCode = request.paymentDetails() != null
                  ? request.paymentDetails().get("BANK").toString()
                  : null;
        if (BANK_CODE_FAIL.equals(bankCode)) {
            return new PaymentProcessorResponse.Failure(BANK_REJECTED,
                    "Bank rejected the transaction registration");
        }
        final String processorRef = String.format("NBK_PROCESSOR: %s",
                RandomizerUtil.randomBase64(16));
        final String redirectRef = String.format("http://REDIRECT_BANK.com/%s", processorRef);
        return new PaymentProcessorResponse.Success(processorRef, redirectRef);
    }
}
