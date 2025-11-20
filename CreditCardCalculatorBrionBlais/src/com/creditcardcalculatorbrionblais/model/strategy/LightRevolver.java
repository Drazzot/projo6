package com.creditcardcalculatorbrionblais.model.strategy;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Pays minimum for 5 months and then full on month 6; repeats. For demo, this strategy does not track global state — caller may maintain cycle count.
 * To keep the interface pure, LightRevolver can be constructed with an initial cycle index.
 */
public class LightRevolver implements PaymentStrategy {
    private int cycleIndex;

    public LightRevolver(int startIndex) { this.cycleIndex = startIndex; }

    @Override
    public PaymentInstruction nextPayment(BigDecimal statementBalance, LocalDate cycleStart, LocalDate cycleEnd) {
        LocalDate payDate = cycleEnd.plusDays(22); // last day of grace
        BigDecimal payment;
        if ((cycleIndex % 6) == 5) {
            payment = statementBalance; // pay full on 6th month
        } else {
            // minimum payment: greater of $30, 3.5% of new balance (approx)
            BigDecimal threePointFive = statementBalance.multiply(new BigDecimal("0.035"));
            BigDecimal min = threePointFive.max(new BigDecimal("30.00"));
            payment = min;
        }
        cycleIndex++;
        return new PaymentInstruction(payment, payDate);
    }
}