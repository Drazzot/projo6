package com.creditcardcalculatorbrionblais.model.strategy;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Payment strategy that makes only minimum payments with occasional late payments.
 * 
 * <p>This strategy represents a cardholder who consistently carries a balance
 * and occasionally pays late, incurring late fees. Payments are always the
 * minimum amount (greater of $30 or 3.5% of balance), and late payments occur
 * on a configurable schedule.</p>
 * 
 * <p>Payment characteristics:</p>
 * <ul>
 *   <li>Amount: Always minimum (greater of $30 or 3.5% of balance)</li>
 *   <li>On-time: cycleEnd + 22 days</li>
 *   <li>Late: cycleEnd + 30 days (incurs late fee)</li>
 *   <li>Interest: Accrues continuously due to revolving balance</li>
 * </ul>
 * 
 * <p>Late payment pattern is controlled by {@code lateEveryNthCycle}. For example,
 * if set to 6, every 6th payment will be late.</p>
 * 
 * @author Brion Blais
 * @version 1.0
 * @since 2024-01-01
 * @see PaymentStrategy
 */
public class HeavyRevolver implements PaymentStrategy {
    
    /** Number of cycles between late payments (0 means never late) */
    private final int lateEveryNthCycle;
    
    /** Current cycle index for tracking late payment schedule */
    private int cycleIndex;

    /**
     * Constructs a new HeavyRevolver with configurable late payment frequency.
     * 
     * @param startIndex the starting cycle index
     * @param lateEveryNthCycle frequency of late payments (e.g., 6 means every 6th is late),
     *                          or 0 to never be late
     */
    public HeavyRevolver(int startIndex, int lateEveryNthCycle) {
        this.cycleIndex = startIndex;
        this.lateEveryNthCycle = lateEveryNthCycle;
    }

    /**
     * Returns a minimum payment instruction, occasionally scheduled late.
     * 
     * <p>Payment logic:</p>
     * <ul>
     *   <li>Amount: max($30, 3.5% of balance)</li>
     *   <li>Date: cycleEnd + 22 days (on-time) or cycleEnd + 30 days (late)</li>
     * </ul>
     * 
     * <p>Late determination: If lateEveryNthCycle > 0 and
     * (cycleIndex % lateEveryNthCycle) == (lateEveryNthCycle - 1), payment is late.</p>
     * 
     * <p>The cycleIndex is incremented after each call.</p>
     * 
     * @param statementBalance the amount due, must not be null
     * @param cycleStart the first day of the billing cycle (unused)
     * @param cycleEnd the last day of the billing cycle, must not be null
     * @return a PaymentInstruction with minimum amount and appropriate date, never null
     * @throws NullPointerException if statementBalance or cycleEnd is null
     */
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