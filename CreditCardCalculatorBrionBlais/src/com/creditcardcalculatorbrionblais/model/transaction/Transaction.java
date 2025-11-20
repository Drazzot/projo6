package com.creditcardcalculatorbrionblais.model.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a single credit card transaction.
 * 
 * <p>A transaction consists of a date, category (such as Groceries, Gas, or Other),
 * and an amount. This class is immutable and thread-safe.</p>
 * 
 * <p>Example usage:</p>
 * <pre>
 * Transaction tx = new Transaction(
 *     LocalDate.of(2024, 1, 15),
 *     "Groceries",
 *     new BigDecimal("125.50")
 * );
 * </pre>
 * 
 * @author Brion Blais
 * @version 1.0
 * @since 2024-01-01
 */
public final class Transaction {
    
    /** The date when the transaction occurred */
    private final LocalDate date;
    
    /** The category of the transaction (e.g., Groceries, Gas, Other) */
    private final String category;
    
    /** The amount of the transaction (positive for purchases) */
    private final BigDecimal amount;

    /**
     * Constructs a new Transaction with the specified details.
     * 
     * @param date the date of the transaction, must not be null
     * @param category the category of the transaction (e.g., "Groceries", "Gas", "Other"),
     *                 may be null for uncategorized transactions
     * @param amount the transaction amount, positive for purchases, must not be null
     * @throws NullPointerException if date or amount is null
     */
    public Transaction(LocalDate date, String category, BigDecimal amount) {
        this.date = date;
        this.category = category;
        this.amount = amount;
    }

    /**
     * Returns the date of this transaction.
     * 
     * @return the transaction date, never null
     */
    public LocalDate getDate() { 
        return date; 
    }
    
    /**
     * Returns the category of this transaction.
     * 
     * @return the transaction category (e.g., "Groceries", "Gas", "Other"),
     *         or null if uncategorized
     */
    public String getCategory() { 
        return category; 
    }
    
    /**
     * Returns the amount of this transaction.
     * 
     * @return the transaction amount, never null
     */
    public BigDecimal getAmount() { 
        return amount; 
    }

    /**
     * Returns a string representation of this transaction.
     * 
     * <p>The format is: "Transaction{date, category, amount}"</p>
     * 
     * @return a string representation including all transaction details
     */
    @Override
    public String toString() {
        return "Transaction{" + date + ", " + category + ", " + amount + "}";
    }
}