package com.creditcardcalculatorbrionblais.model.strategy;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Payment strategy that alternates between minimum payments and full payoff.
 * 
 * <p>This strategy represents a cardholder who carries a balance most of the time
 * (making minimum payments) but periodically pays off the full balance. The pattern
 * is: 5 months of minimum payments followed by 1 month of full payment, repeating.</p>
 * 
 * <p>Payment characteristics:</p>
 * <ul>
 *   <li>Months 1-5: Minimum payment (greater of $30 or 3.5% of balance)</li>
 *   <li>Month 6: Full statement balance</li>
 *   <li>Timing: Last day of grace period (cycleEnd + 22 days)</li>
 *   <li>Interest: Accrues during months 1-5, cleared in month 6</li>
 * </ul>
 * 
 * <p>This strategy maintains internal state via cycleIndex to track position
 * in the 6-month cycle.</p>
 * 
 * @author Brion Blais
 * @version 1.0
 * @since 2024-01-01
 * @see PaymentStrategy
 */
public class LightRevolver implements PaymentStrategy {
    
    /** Current position in the 6-month payment cycle */
    private int cycleIndex;

    /**
     * Constructs a new LightRevolver starting at the specified cycle index.
     * 
     * <p>The cycle repeats every 6 months (indices 0-5). Index 5 results in
     * a full payment, all others result in minimum payments.</p>
     * 
     * @param startIndex the starting cycle index (0-5 for typical usage)
     */
    public LightRevolver(int startIndex) { 
        this.cycleIndex = startIndex; 
    }

    /**
     * Returns a payment instruction based on the current cycle position.
     * 
     * <p>Payment logic:</p>
     * <ul>
     *   <li>If (cycleIndex % 6) == 5: Pay full balance</li>
     *   <li>Otherwise: Pay minimum (max of $30 or 3.5% of balance)</li>
     * </ul>
     * 
     * <p>The cycleIndex is incremented after each call, automatically
     * advancing through the payment cycle.</p>
     * 
     * @param statementBalance the amount due, must not be null
     * @param cycleStart the first day of the billing cycle (unused)
     * @param cycleEnd the last day of the billing cycle, must not be null
     * @return a PaymentInstruction with the calculated amount, never null
     * @throws NullPointerException if statementBalance or cycleEnd is null
     */
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