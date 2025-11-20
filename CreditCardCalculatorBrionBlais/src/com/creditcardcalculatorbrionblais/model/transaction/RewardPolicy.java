package com.creditcardcalculatorbrionblais.model.transaction;

import java.math.BigDecimal;
import java.util.Locale;

public class RewardPolicy {
    private final BigDecimal groceriesRate;
    private final BigDecimal gasRate;
    private final BigDecimal otherRate;

    public RewardPolicy(BigDecimal groceriesRate, BigDecimal gasRate, BigDecimal otherRate) {
        this.groceriesRate = groceriesRate;
        this.gasRate = gasRate;
        this.otherRate = otherRate;
    }

    public static RewardPolicy defaultPolicy() {
        return new RewardPolicy(new BigDecimal("0.03"), new BigDecimal("0.02"), new BigDecimal("0.01"));
    }

    public BigDecimal rewardFor(String category, BigDecimal amount) {
        if (category == null) return amount.multiply(otherRate);
        String c = category.trim().toLowerCase(Locale.ROOT);
        switch (c) {
            case "groceries": return amount.multiply(groceriesRate);
            case "gas": return amount.multiply(gasRate);
            default: return amount.multiply(otherRate);
        }
    }
}
