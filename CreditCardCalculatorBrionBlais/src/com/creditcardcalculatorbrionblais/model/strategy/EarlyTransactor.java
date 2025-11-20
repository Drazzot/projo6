package com.creditcardcalculatorbrionblais.model.strategy;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Payment strategy that pays the full statement balance early.
 * 
 * <p>This strategy represents a disciplined cardholder who pays their
 * full balance at the beginning of the grace period (modeled as the day
 * after the cycle ends). This behavior minimizes interest charges and
 * maintains good credit standing.</p>
 * 
 * <p>Payment characteristics:</p>
 * <ul>
 *   <li>Amount: Full statement balance</li>
 *   <li>Timing: First day of grace period (cycleEnd + 1 day)</li>
 *   <li>Interest: None, if grace period is honored</li>
 * </ul>
 * 
 * @author Brion Blais
 * @version 1.0
 * @since 2024-01-01
 * @see PaymentStrategy
 */
public class EarlyTransactor implements PaymentStrategy {
    
    /**
     * Returns a payment instruction for the full balance on the first day of grace.
     * 
     * <p>The payment date is calculated as cycleEnd + 1 day, representing the
     * earliest possible payment date within the grace period.</p>
     * 
     * @param statementBalance the full amount due, must not be null
     * @param cycleStart the first day of the billing cycle (unused)
     * @param cycleEnd the last day of the billing cycle, must not be null
     * @return a PaymentInstruction for the full balance, never null
     * @throws NullPointerException if statementBalance or cycleEnd is null
     */
    @Override
    public PaymentInstruction nextPayment(BigDecimal statementBalance, LocalDate cycleStart, LocalDate cycleEnd) {
        LocalDate payDate = cycleEnd.plusDays(1); // first day of grace
        return new PaymentInstruction(statementBalance, payDate);
    }
}