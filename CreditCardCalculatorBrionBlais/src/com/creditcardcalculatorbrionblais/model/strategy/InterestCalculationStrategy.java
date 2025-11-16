package com.creditcardcalculatorbrionblais.model.strategy;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface InterestCalculationStrategy {
    /**
     * Compute interest for the cycle given starting balance and apr.
     * Returns InterestResult with totalInterest and daily breakdown if needed.
     */
    InterestResult computeInterest(BigDecimal startingBalance, BigDecimal apr, LocalDate cycleStart, LocalDate cycleEnd);
}
