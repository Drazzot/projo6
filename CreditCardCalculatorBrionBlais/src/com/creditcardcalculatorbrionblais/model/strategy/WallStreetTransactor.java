package com.creditcardcalculatorbrionblais.model.strategy;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Payment strategy that pays the full balance on the last day of the grace period.
 * 
 * <p>This strategy represents a financially savvy cardholder who maximizes the
 * float by keeping money in their account as long as possible while still
 * avoiding interest charges. Payment is made on the last possible day before
 * the grace period expires.</p>
 * 
 * <p>Payment characteristics:</p>
 * <ul>
 *   <li>Amount: Full statement balance</li>
 *   <li>Timing: Last day of grace period (cycleEnd + 22 days)</li>
 *   <li>Interest: None, if payment clears before grace period ends</li>
 * </ul>
 * 
 * <p>The 22-day offset represents a typical grace period where the due date
 * is at least 23 days after the cycle closes.</p>
 * 
 * @author Brion Blais
 * @version 1.0
 * @since 2024-01-01
 * @see PaymentStrategy
 */
public class WallStreetTransactor implements PaymentStrategy {
    
    /**
     * Returns a payment instruction for the full balance on the last day of grace.
     * 
     * <p>The payment date is calculated as cycleEnd + 22 days, representing
     * the last day before the typical 23-day grace period expires.</p>
     * 
     * @param statementBalance the full amount due, must not be null
     * @param cycleStart the first day of the billing cycle (unused)
     * @param cycleEnd the last day of the billing cycle, must not be null
     * @return a PaymentInstruction for the full balance, never null
     * @throws NullPointerException if statementBalance or cycleEnd is null
     */
    @Override
    public PaymentInstruction nextPayment(BigDecimal statementBalance, LocalDate cycleStart, LocalDate cycleEnd) {
        LocalDate payDate = cycleEnd.plusDays(22); // last day of grace period
        return new PaymentInstruction(statementBalance, payDate);
    }
}