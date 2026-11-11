package com.saipramod.day01;

public record CardPayment(String last4, boolean international) implements PaymentMethod {
}
