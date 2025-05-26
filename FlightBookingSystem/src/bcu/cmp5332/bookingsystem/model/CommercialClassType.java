package bcu.cmp5332.bookingsystem.model;

import java.math.BigDecimal;

public enum CommercialClassType {
    ECONOMY("Economy", BigDecimal.valueOf(1.0)),
    PREMIUM_ECONOMY("Premium Economy", BigDecimal.valueOf(1.6)), // 60% more
    BUSINESS("Business", BigDecimal.valueOf(3.5)), // 3.5 times economy
    FIRST("First", BigDecimal.valueOf(7.5)); // 7.5 times economy

    private final String className;
    private final BigDecimal multiplier;

    CommercialClassType(String className, BigDecimal multiplier) {
        this.className = className;
        this.multiplier = multiplier;
    }

    public String getClassName() {
        return className;
    }

    public BigDecimal getMultiplier() {
        return multiplier;
    }

    @Override
    public String toString() {
        return className;
    }
}