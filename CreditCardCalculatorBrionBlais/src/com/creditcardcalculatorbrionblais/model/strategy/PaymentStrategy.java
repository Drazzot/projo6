package com.creditcardcalculatorbrionblais.model.strategy;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * PaymentStrategy: decide payment amount and date for each statement cycle.
 */
public interface PaymentStrategy {
    /**
     * Called each billing cycle to compute payment amount and the date (relative to cycle).
     * @param statementBalance the amount due on statement
     * @param cycleStart first day of cycle
     * @param cycleEnd last day of cycle
     * @return PaymentInstruction
     */
    PaymentInstruction nextPayment(BigDecimal statementBalance, LocalDate cycleStart, LocalDate cycleEnd);
}