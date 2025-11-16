package com.creditcardcalculatorbrionblais.model.strategy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Simplified synchrony daily-balance interest calculation.
 * This version assumes no intra-cycle payments other than those already applied to starting balance
 * and charges (we handle basic step-by-step compounding).
 */
public class SynchronyDailyBalanceStrategy implements InterestCalculationStrategy {

    @Override
    public InterestResult computeInterest(BigDecimal startingBalance, BigDecimal apr, LocalDate cycleStart, LocalDate cycleEnd) {
        BigDecimal dailyRate = apr.divide(BigDecimal.valueOf(365), 10, BigDecimal.ROUND_HALF_UP);
        List<BigDecimal> daily = new ArrayList<>();
        BigDecimal bal = startingBalance;
        LocalDate d = cycleStart;
        BigDecimal sum = BigDecimal.ZERO;
        while (!d.isAfter(cycleEnd)) {
            BigDecimal dailyInterest = BigDecimal.ZERO;
            if (bal.signum() > 0) {
                dailyInterest = bal.multiply(dailyRate);
                dailyInterest = dailyInterest.setScale(10, BigDecimal.ROUND_HALF_UP);
                sum = sum.add(dailyInterest);
                bal = bal.add(dailyInterest);
            }
            daily.add(dailyInterest);
            d = d.plusDays(1);
        }
        // minimum interest charge
        if (sum.compareTo(BigDecimal.ZERO) > 0 && sum.compareTo(BigDecimal.valueOf(2.00)) < 0) {
            sum = BigDecimal.valueOf(2.00);
        }
        sum = sum.setScale(2, BigDecimal.ROUND_HALF_UP);
        return new InterestResult(sum, daily);
    }
}
