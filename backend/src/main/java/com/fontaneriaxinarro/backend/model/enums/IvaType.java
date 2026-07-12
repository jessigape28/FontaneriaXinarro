package com.fontaneriaxinarro.backend.model.enums;

import java.math.BigDecimal;

public enum IvaType {
    EXEMPT(BigDecimal.ZERO),
    REDUCED_10(BigDecimal.valueOf(10)),
    GENERAL_21(BigDecimal.valueOf(21));

    private final BigDecimal percentage;

    IvaType(BigDecimal percentage) {
        this.percentage = percentage;
    }

    public BigDecimal getPorcentaje() {
        return percentage;
    }
}
