package com.codingshuttle.razorpay.payment.processor.strategy;

import com.codingshuttle.razorpay.common.util.RandomizerUtil;
import com.codingshuttle.razorpay.payment.processor.PaymentProcessor;
import com.codingshuttle.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.codingshuttle.razorpay.payment.processor.dto.PaymentProcessorResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CardPaymentProcessor implements PaymentProcessor {

    public static final String PAN_CARD_DECLINED = "400000000002";
    public static final String PAN_CARD_EXPIRED = "400000000069";

    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {
        switch(request.pan()) {
            case PAN_CARD_DECLINED: {
                log.warn("Card Declined. Card number: {}", request.pan());
                return new PaymentProcessorResponse.Failure("CARD_DECLINED", "Card Declined");
            }
            case PAN_CARD_EXPIRED: {
                log.warn("Card Expired. Card number: {}", request.pan());
                return new PaymentProcessorResponse.Failure("CARD_EXPIRED", "Card Expired");
            }
            default: {
                final String processorRef = String.format("CARD PROCESSOR %s", RandomizerUtil.randomBase64(32));
                return new PaymentProcessorResponse.Pending(processorRef);
            }
        }
    }
}
