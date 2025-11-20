package com.creditcardcalculatorbrionblais.model.strategy;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Pays the full statement balance at the first day of the grace period (we model as cycleEnd + 1).
 */
public class EarlyTransactor implements PaymentStrategy {
    @Override
    public PaymentInstruction nextPayment(BigDecimal statementBalance, LocalDate cycleStart, LocalDate cycleEnd) {
        LocalDate payDate = cycleEnd.plusDays(1); // first day of grace
        return new PaymentInstruction(statementBalance, payDate);
    }
}