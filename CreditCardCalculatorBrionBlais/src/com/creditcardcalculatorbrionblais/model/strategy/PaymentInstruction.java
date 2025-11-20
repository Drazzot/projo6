package com.creditcardcalculatorbrionblais.model.strategy;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a payment instruction specifying amount and date.
 * 
 * <p>This class encapsulates the result of a PaymentStrategy decision,
 * indicating how much to pay and when to pay it. Instances are immutable.</p>
 * 
 * <p>Example usage:</p>
 * <pre>
 * PaymentInstruction payment = new PaymentInstruction(
 *     new BigDecimal("150.00"),
 *     LocalDate.of(2024, 2, 15)
 * );
 * </pre>
 * 
 * @author Brion Blais
 * @version 1.0
 * @since 2024-01-01
 * @see PaymentStrategy
 */
public class PaymentInstruction {
    
    /** The amount to be paid */
    private final BigDecimal amount;
    
    /** The date when the payment should be applied */
    private final LocalDate date;

    /**
     * Constructs a new PaymentInstruction with the specified amount and date.
     * 
     * @param amount the payment amount, must not be null
     * @param date the payment date, must not be null
     * @throws NullPointerException if amount or date is null
     */
    public PaymentInstruction(BigDecimal amount, LocalDate date) {
        this.amount = amount;
        this.date = date;
    }

    /**
     * Returns the payment amount.
     * 
     * @return the amount to be paid, never null
     */
    public BigDecimal getAmount() { 
        return amount; 
    }
    
    /**
     * Returns the payment date.
     * 
     * @return the date when payment should be applied, never null
     */
    public LocalDate getDate() { 
        return date; 
    }
}