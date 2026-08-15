package com.codingshuttle.razorpay.payment.service.impl;

import com.codingshuttle.razorpay.common.enums.OrderStatus;
import com.codingshuttle.razorpay.payment.dto.request.CreateOrderRequest;
import com.codingshuttle.razorpay.payment.dto.response.OrderResponse;
import com.codingshuttle.razorpay.payment.entity.OrderRecord;
import com.codingshuttle.razorpay.payment.repository.OrderRepository;
import com.codingshuttle.razorpay.payment.service.OrderService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Value("${payment.order.default-expiry-minutes:30}")
    private int defaultOrderExpiryMinutes;

    @Override
    public OrderResponse create(UUID merchantId, CreateOrderRequest request) {
        if (StringUtils.isNotEmpty(request.receipt()) && orderRepository.existsByMerchantIdAndReceipt(merchantId, request.receipt())) {
            throw new IllegalArgumentException("Order with the same receipt already exists for this merchant.");
        }

        final OrderRecord orderRecord = OrderRecord.builder()
                .merchantId(merchantId)
                .amount(request.amount())
                .receipt(request.receipt())
                .orderStatus(OrderStatus.CREATED)
                .notes(request.notes())
                .expiresAt(request.expiresAt() != null ? request.expiresAt() : LocalDateTime.now().plusMinutes(
                        defaultOrderExpiryMinutes))
                .build();
        orderRepository.save(orderRecord);

        //TODO: SEND KAFKA EVENT THAT ORDER IS CREATED
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
