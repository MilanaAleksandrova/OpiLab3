package org.milana.weblab3.utils;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public enum Bounds {
    X_BOUNDS(BigDecimal.valueOf(-4), BigDecimal.valueOf(4), true),
    Y_BOUNDS(BigDecimal.valueOf(-5), BigDecimal.valueOf(3), false),
    R_BOUNDS(BigDecimal.valueOf(1), BigDecimal.valueOf(5), true);

    private final BigDecimal left;
    private final BigDecimal right;
    private final boolean inclusive;

    Bounds(BigDecimal left, BigDecimal right, boolean inclusive) {
        this.left = left;
        this.right = right;
        this.inclusive = inclusive;
    }
}
