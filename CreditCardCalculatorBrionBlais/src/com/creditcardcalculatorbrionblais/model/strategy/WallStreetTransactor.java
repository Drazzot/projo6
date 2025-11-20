package com.creditcardcalculatorbrionblais.model.strategy;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Pays the full statement balance on the last day of the grace period (modeled as cycleEnd plus grace length).
 * For simplicity, we'll use cycleEnd.plusDays(22) to represent typical ~23 day grace (due date at least 23 days after close).
 */
public class WallStreetTransactor implements PaymentStrategy {
    @Override
    public PaymentInstruction nextPayment(BigDecimal statementBalance, LocalDate cycleStart, LocalDate cycleEnd) {
        LocalDate payDate = cycleEnd.plusDays(22); // last day of grace period
        return new PaymentInstruction(statementBalance, payDate);
    }
}