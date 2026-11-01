package com.codingshuttle.razorpay.payment.simulator;

import com.codingshuttle.razorpay.common.enums.PaymentStatus;
import com.codingshuttle.razorpay.payment.entity.Payment;
import com.codingshuttle.razorpay.payment.repository.PaymentRepository;
import com.codingshuttle.razorpay.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class BankCallbackSimulator {

    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;
    private final SimulatorConfig simulatorConfig;

    @Scheduled(fixedRate = 5000)
    public void processCallbacks() {
        final LocalDateTime globalWindow = LocalDateTime.now().minusSeconds(1);
        final List<Payment> payments = paymentRepository.findByStatusAndCreatedAtBefore(PaymentStatus.AUTHORIZING,
                                                                                         globalWindow);
        if (payments.isEmpty()) {
            return;
        }
        payments.forEach(this::simulateCallback);
    }

    private void simulateCallback(Payment payment) {

    }
}
