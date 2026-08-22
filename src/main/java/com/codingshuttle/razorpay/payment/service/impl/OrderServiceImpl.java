package com.codingshuttle.razorpay.payment.service.impl;

import com.codingshuttle.razorpay.common.enums.OrderStatus;
import com.codingshuttle.razorpay.common.exception.BusinessRuleViolationException;
import com.codingshuttle.razorpay.payment.dto.request.CreateOrderRequest;
import com.codingshuttle.razorpay.payment.dto.response.OrderResponse;
import com.codingshuttle.razorpay.payment.dto.response.PaymentResponse;
import com.codingshuttle.razorpay.payment.entity.OrderRecord;
import com.codingshuttle.razorpay.payment.repository.OrderRepository;
import com.codingshuttle.razorpay.payment.service.OrderService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Value("${payment.order.default-expiry-minutes:30}")
    private int defaultOrderExpiryMinutes;

    @Override
    public OrderResponse create(final UUID merchantId, final CreateOrderRequest request) {
        if (StringUtils.isNotEmpty(request.receipt()) && orderRepository.existsByMerchantIdAndReceipt(merchantId, request.receipt())) {
            throw new IllegalArgumentException("Order with the same receipt already exists for this merchant.");
        }

        OrderRecord orderRecord = OrderRecord.builder()
                .merchantId(merchantId)
                .amount(request.amount())
                .receipt(request.receipt())
                .orderStatus(OrderStatus.CREATED)
                .notes(request.notes())
                .expiresAt(request.expiresAt() != null ? request.expiresAt() : LocalDateTime.now().plusMinutes(
                        defaultOrderExpiryMinutes))
                .build();
        orderRecord = orderRepository.save(orderRecord);

        //TODO: SEND KAFKA EVENT THAT ORDER IS CREATED
        return getOrderResponse(orderRecord);
    }

    @Override
    public OrderResponse getById(UUID merchantId, UUID orderId) {
        final OrderRecord orderRecord = orderRepository.findByIdAndMerchantId(orderId, merchantId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found for the given merchant and order ID."));
        return getOrderResponse(orderRecord);

    }

    @Override
    public OrderResponse cancel(UUID merchantId, UUID orderId) {
        OrderRecord orderRecord = orderRepository.findByIdAndMerchantId(orderId, merchantId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found for the given merchant and order ID."));
        if (orderRecord.getOrderStatus() == OrderStatus.CANCELLED || orderRecord.getOrderStatus() == OrderStatus.PAID) {
            throw new BusinessRuleViolationException("Order cannot be cancelled as it is already " + orderRecord.getOrderStatus(),
                    "ORDER_CANNOT_BE_CANCELLED");
        }
        orderRecord.setOrderStatus(OrderStatus.CANCELLED);
        orderRecord = orderRepository.save(orderRecord);
       return getOrderResponse(orderRecord);
    }

    @Override
    public List<PaymentResponse> listPayments(UUID merchantId, UUID orderId) {
        return List.of();
    }

    private OrderResponse getOrderResponse(OrderRecord orderRecord) {
        return OrderResponse.builder()
                .id(orderRecord.getId())
                .merchantId(orderRecord.getMerchantId())
                .amount(orderRecord.getAmount())
                .receipt(orderRecord.getReceipt())
                .orderStatus(orderRecord.getOrderStatus())
                .attempts(orderRecord.getAttempts())
                .notes(orderRecord.getNotes())
                .expiresAt(orderRecord.getExpiresAt())
                .build();
    }
}
