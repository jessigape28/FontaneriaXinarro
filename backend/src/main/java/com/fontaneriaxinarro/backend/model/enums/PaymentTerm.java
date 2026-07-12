package com.fontaneriaxinarro.backend.model.enums;

public enum PaymentTerm {
    CASH(0),
    NET_15(15),
    NET_30(30),
    NET_60(60);

    private final int days;

    PaymentTerm(int days) {
        this.days = days;
    }

    public int getDays() {
        return days;
    }
}
