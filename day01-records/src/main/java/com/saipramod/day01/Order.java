package com.saipramod.day01;

public record Order(String id, Money total, PaymentMethod paymentMethod) {
}
