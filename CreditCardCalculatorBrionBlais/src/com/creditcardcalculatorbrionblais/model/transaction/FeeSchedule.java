package com.creditcardcalculatorbrionblais.model.transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Encapsulates Synchrony-derived defaults and fee rules.
 */
public class FeeSchedule {
    // APRs
    public static final BigDecimal APR_PURCHASES = new BigDecimal("0.3499"); // 34.99%
    public static final BigDecimal APR_PENALTY = new BigDecimal("0.3999"); // 39.99%

    // Minimum interest charge
    public static final BigDecimal MIN_INTEREST_CHARGE = new BigDecimal("2.00");

    // Paper statement fee per month
    public static final BigDecimal PAPER_STATEMENT_FEE = new BigDecimal("1.99");

    // Promotional fee percent for long promos (presented as 0.02 == 2%)
    public static final BigDecimal PROMOTIONAL_FEE_RATE = new BigDecimal("0.02");

    // Late/returned payment fee tiers
    public static final BigDecimal LATE_FEE_LOW = new BigDecimal("30.00");
    public static final BigDecimal LATE_FEE_HIGH = new BigDecimal("41.00");

    // Rounding helper
    public static BigDecimal roundCurrency(BigDecimal v) {
        if (v == null) return null;
        return v.setScale(2, RoundingMode.HALF_UP);
    }
}