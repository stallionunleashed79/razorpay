package com.codingshuttle.razorpay.operations.entity;

import com.codingshuttle.razorpay.common.entity.BaseAuditEntity;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettlementPaymentId extends BaseAuditEntity implements Serializable  {

    @Column(name = "settlement_id", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID settlementId;

    @Column(name = "payment_id", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID paymentId;
}
