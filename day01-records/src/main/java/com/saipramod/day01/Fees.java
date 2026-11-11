package com.saipramod.day01;

import java.math.BigDecimal;

public class Fees {
    static final BigDecimal INTL_CARD_FEE = new BigDecimal("2.90");
    static final BigDecimal CARD_FEE = new BigDecimal("1.50");
    static final BigDecimal CASH_FEE = new BigDecimal("0.50");

    public static BigDecimal feeFor(PaymentMethod method) {
        return switch (method) {
            case null -> throw new IllegalArgumentException("payment method must not be null");
            case CardPayment card when card.international() -> INTL_CARD_FEE;
            case CardPayment card -> CARD_FEE;
            case UpiPayment upi -> BigDecimal.ZERO;
            case CashPayment cash -> CASH_FEE;
        };
    }
}
