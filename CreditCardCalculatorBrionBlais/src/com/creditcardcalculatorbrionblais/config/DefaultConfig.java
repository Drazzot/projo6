package com.creditcardcalculatorbrionblais.config;

import java.math.BigDecimal;

public final class DefaultConfig {
    public static final BigDecimal APR = new BigDecimal("0.35");
    public static final BigDecimal PENALTY_APR = new BigDecimal("0.3999");
    public static final int BILLING_CYCLE_DAYS = 30;
    public static final int DUE_DAY = 20;
    public static final BigDecimal REWARD_GROCERIES = new BigDecimal("0.03");
    public static final BigDecimal REWARD_GAS = new BigDecimal("0.02");
    public static final BigDecimal REWARD_OTHER = new BigDecimal("0.01");
    public static final java.math.BigDecimal PAPER_STATEMENT_FEE = new java.math.BigDecimal("1.99");
    public static final java.math.BigDecimal MIN_INTEREST_CHARGE = new java.math.BigDecimal("2.00");
    private DefaultConfig() {}
}
