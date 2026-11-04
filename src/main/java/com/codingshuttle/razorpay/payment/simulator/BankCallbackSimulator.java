package com.codingshuttle.razorpay.payment.simulator;

import com.codingshuttle.razorpay.common.enums.ChaosMode;
import com.codingshuttle.razorpay.common.enums.PaymentStatus;
import com.codingshuttle.razorpay.common.util.RandomizerUtil;
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

    @Scheduled(fixedDelayString = "${payment.simulator.poll-interval-ms:5000}")
    public void processCallbacks() {
        final LocalDateTime globalWindow = LocalDateTime.now().minusSeconds(1);
        final List<Payment> payments = paymentRepository.findByStatusAndCreatedAtBefore(PaymentStatus.AUTHORIZING,
                                                                                         globalWindow);
        if (payments.isEmpty()) {
            return;
        }
        payments.forEach(this::simulateCallback);
    }

    private void simulateCallback(final Payment payment) {
      final SimulatorConfig.MethodSimulatorConfig methodSimulatorConfig = simulatorConfig.getConfigForMethod(
             payment.getPaymentMethod());
      final LocalDateTime dueAt = dueAt(payment, methodSimulatorConfig);
      if (LocalDateTime.now().isBefore(dueAt)) {
          return;
      }

      switch (simulatorConfig.getChaosMode()) {
          case SUCCESS -> resolve(payment, true);
          case FAILURE -> resolve(payment, false);
          case TIMEOUT -> log.info("Simulating slow or timeout for payment {}", payment.getId());
          case NORMAL, SLOW -> resolve(payment, shouldApprove(payment, methodSimulatorConfig));
      }
    }

    private void resolve(final Payment payment, final boolean approve) {
        if (approve) {
            String bankRef = "SIM_BANK_REF" + RandomizerUtil.randomBase64(8);
            paymentService.resolveAuthorization(payment.getId(), true, bankRef, null, null);
        } else {
            paymentService.resolveAuthorization(payment.getId(), false, null, "SIM_BANK_ERR_CODE", "Simulated Bank Declined");
        }
    }

    private boolean shouldApprove(final Payment payment, final SimulatorConfig.MethodSimulatorConfig methodSimulatorConfig) {
        int bucket = Math.abs(payment.getId().hashCode()) % 100;
        return bucket < methodSimulatorConfig.getSuccessRate();
    }

    private LocalDateTime dueAt(final Payment payment, final SimulatorConfig.MethodSimulatorConfig methodSimulatorConfig) {
        int range = methodSimulatorConfig.getMaxDelaySeconds() - methodSimulatorConfig.getMinDelaySeconds();
        int delayInSeconds = methodSimulatorConfig.getMinDelaySeconds() + Math.abs(payment.getId().hashCode()) % (range+1);

        if (simulatorConfig.getChaosMode() == ChaosMode.SLOW) {
            delayInSeconds *= 2;
        }

        return payment.getCreatedAt().plusSeconds(delayInSeconds);
    }
}
