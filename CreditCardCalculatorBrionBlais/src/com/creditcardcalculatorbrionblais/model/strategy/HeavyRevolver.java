package com.creditcardcalculatorbrionblais.model.strategy;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Pays only minimum; occasionally pays late (we model as configurable lateEveryNthCycle).
 */
public class HeavyRevolver implements PaymentStrategy {
    private final int lateEveryNthCycle;
    private int cycleIndex;

    public HeavyRevolver(int startIndex, int lateEveryNthCycle) {
        this.cycleIndex = startIndex;
        this.lateEveryNthCycle = lateEveryNthCycle;
    }

    @Override
    public PaymentInstruction nextPayment(BigDecimal statementBalance, LocalDate cycleStart, LocalDate cycleEnd) {
        boolean late = (lateEveryNthCycle > 0) && ((cycleIndex % lateEveryNthCycle) == (lateEveryNthCycle - 1));
        LocalDate payDate = late ? cycleEnd.plusDays(30) : cycleEnd.plusDays(22);
        BigDecimal threePointFive = statementBalance.multiply(new BigDecimal("0.035"));
        BigDecimal min = threePointFive.max(new BigDecimal("30.00"));
        cycleIndex++;
        return new PaymentInstruction(min, payDate);
    }
}