package com.codingshuttle.razorpay.common.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Currency;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Embeddable
public class Money {

    private int amountUtils;
    private Currency currency;

    public Money add(Money money) {
       if (!this.currency.equals(money.currency)) {
           throw new IllegalArgumentException("Currency mismatch");
       }
       return new Money(this.amountUtils + money.amountUtils, this.currency);
    }

    public Money subtract(Money money) {
        if (!this.currency.equals(money.currency)) {
            throw new IllegalArgumentException("Currency mismatch");
        }
        return new Money(this.amountUtils - money.amountUtils, this.currency);
    }
}
