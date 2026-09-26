package com.codingshuttle.razorpay.payment.gateway.adapter.impl;

import com.codingshuttle.razorpay.common.enums.PaymentMethod;
import com.codingshuttle.razorpay.payment.gateway.adapter.PaymentAdapter;
import com.codingshuttle.razorpay.payment.gateway.adapter.factory.PaymentProcessorRouter;
import org.springframework.stereotype.Component;

@Component
public class UPIPaymentAdapter extends PaymentAdapter {
  public UPIPaymentAdapter(PaymentProcessorRouter paymentProcessorRouter) {
    super(paymentProcessorRouter);
  }

  @Override
  protected PaymentMethod getPaymentMethod() {
    return PaymentMethod.UPI;
  }
}
