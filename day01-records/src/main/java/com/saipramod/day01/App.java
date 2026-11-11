package com.saipramod.day01;

import java.math.BigDecimal;

public class App {
    public static void main(String[] args) {
        Money price = new Money(new BigDecimal("49.99"), "USD");
        System.out.println(price);
        System.out.println("equal: " + price.equals(new Money(new BigDecimal("49.99"), "USD")));
        System.out.println("amount accessor: " + price.amount());

        Order order = new Order("ORD-1", price, new CardPayment("4242", true));
        System.out.println(order);

        System.out.println("intl card fee: " + Fees.feeFor(new CardPayment("4242", true)));
        System.out.println("domestic card fee: " + Fees.feeFor(new CardPayment("4242", false)));
        System.out.println("upi fee: " + Fees.feeFor(new UpiPayment("saip@okhdfc")));
        System.out.println("cash fee: " + Fees.feeFor(new CashPayment()));

        System.out.println("nothing: " + Describer.describe(null));
        System.out.println(Describer.describe("hello"));
        System.out.println(Describer.describe(new Money(new BigDecimal("10.00"), "USD")));
        System.out.println(Describer.describe(42));
    }
}
