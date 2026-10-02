package com.codingshuttle.razorpay.payment.service;

import com.codingshuttle.razorpay.common.enums.PaymentStatus;
import com.codingshuttle.razorpay.payment.entity.Payment;
import com.codingshuttle.razorpay.payment.entity.PaymentActor;
import com.codingshuttle.razorpay.payment.entity.PaymentEvent;
import com.codingshuttle.razorpay.payment.entity.PaymentTransitionLog;
import com.codingshuttle.razorpay.payment.repository.PaymentRepository;
import com.codingshuttle.razorpay.payment.repository.PaymentTransitionLogRepository;
import com.codingshuttle.razorpay.payment.statemachine.PaymentStateMachine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentTransitionService {

    private final PaymentTransitionLogRepository paymentTransitionLogRepository;
    private final PaymentStateMachine paymentStateMachine;
    private final PaymentRepository paymentRepository;

    public PaymentStatus apply(final Payment payment, final PaymentEvent paymentEvent) {
        final PaymentStatus paymentStatus = paymentStateMachine.getNextState(
                payment.getStatus(), paymentEvent
        );
        payment.setStatus(paymentStatus);
        paymentRepository.save(payment);
        final PaymentTransitionLog paymentTransitionLog = PaymentTransitionLog.builder()
                .payment(payment)
                .fromStatus(payment.getStatus())
                .toStatus(paymentStatus)
                .event(paymentEvent)
                .actor(PaymentActor.SYSTEM) //TODO: Fetch merchant Id from request to identify actor
                .occuredAt(LocalDateTime.now())
                .build();
        paymentTransitionLogRepository.save(paymentTransitionLog);
        return paymentStatus;
    }
}
