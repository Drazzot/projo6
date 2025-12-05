package com.creditcardcalculatorbrionblais.model.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class DailyBalanceInterestCalculator implements InterestCalculator {

    @Override
    public BigDecimal calculateInterest(
            BigDecimal balance,
            LocalDate cycleStart,
            LocalDate cycleEnd,
            BigDecimal apr
    ) {
        BigDecimal days = new BigDecimal(cycleEnd.getDayOfMonth());
        BigDecimal dailyRate = apr.divide(new BigDecimal("365"), 10, RoundingMode.HALF_UP);

        BigDecimal interest = balance.multiply(dailyRate).multiply(days);

        return interest.setScale(2, RoundingMode.HALF_UP);
    }
}
