package com.creditcardcalculatorbrionblais.model.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class AverageDailyBalanceCalculator implements InterestCalculator {

    @Override
    public BigDecimal calculateInterest(
            BigDecimal averageDailyBalance,
            LocalDate cycleStart,
            LocalDate cycleEnd,
            BigDecimal apr
    ) {
        BigDecimal days = new BigDecimal(cycleEnd.getDayOfMonth());
        BigDecimal dailyRate = apr.divide(new BigDecimal("365"), 10, RoundingMode.HALF_UP);

        return averageDailyBalance.multiply(dailyRate)
                                  .multiply(days)
                                  .setScale(2, RoundingMode.HALF_UP);
    }
}
