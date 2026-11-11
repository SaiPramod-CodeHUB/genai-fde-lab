package com.saipramod.day01;

public class Describer {
    public static String describe(Object o) {
        if (o == null) {
            return "null value";
        }
        if (o instanceof String s) {
            return "text: " + s + " (length " + s.length() + ")";
        }
        if (o instanceof Money money) {
            return "money: " + money.amount() + " " + money.currency();
        }
        return "something else: " + o.getClass().getSimpleName();
    }
}
