package com.creditcardcalculatorbrionblais.model.strategy;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Strategy interface for determining payment amounts and dates.
 * 
 * <p>Implementations of this interface define different payment behaviors such as:</p>
 * <ul>
 *   <li>Paying the full balance early (EarlyTransactor)</li>
 *   <li>Paying the full balance at the last moment (WallStreetTransactor)</li>
 *   <li>Paying minimum amounts with periodic full payments (LightRevolver)</li>
 *   <li>Paying minimum amounts with occasional late payments (HeavyRevolver)</li>
 * </ul>
 * 
 * <p>This follows the Strategy design pattern, allowing different payment behaviors
 * to be easily swapped and tested.</p>
 * 
 * @author Brion Blais
 * @version 1.0
 * @since 2024-01-01
 * @see EarlyTransactor
 * @see WallStreetTransactor
 * @see LightRevolver
 * @see HeavyRevolver
 */
public interface PaymentStrategy {
    
    /**
     * Determines the payment amount and date for a billing cycle.
     * 
     * <p>This method is called once per billing cycle to determine how much
     * should be paid and when. The implementation may maintain internal state
     * to track payment patterns over time.</p>
     * 
     * @param statementBalance the total amount due on the statement, must not be null
     * @param cycleStart the first day of the billing cycle, must not be null
     * @param cycleEnd the last day of the billing cycle, must not be null
     * @return a PaymentInstruction specifying amount and date, never null
     * @throws NullPointerException if any parameter is null
     */
    PaymentInstruction nextPayment(BigDecimal statementBalance, LocalDate cycleStart, LocalDate cycleEnd);
}