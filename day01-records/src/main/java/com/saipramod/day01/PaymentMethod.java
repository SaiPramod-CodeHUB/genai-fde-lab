package com.saipramod.day01;

public sealed interface PaymentMethod permits CardPayment, UpiPayment, CashPayment {
}
