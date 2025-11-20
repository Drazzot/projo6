package com.creditcardcalculatorbrionblais.model.transaction;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * Defines the reward rates for different transaction categories.
 * 
 * <p>This policy determines how much cashback or rewards a user earns based on
 * the category of their purchase. Typical categories include Groceries, Gas, and Other.</p>
 * 
 * <p>Example usage:</p>
 * <pre>
 * RewardPolicy policy = new RewardPolicy(
 *     new BigDecimal("0.03"),  // 3% for groceries
 *     new BigDecimal("0.02"),  // 2% for gas
 *     new BigDecimal("0.01")   // 1% for other
 * );
 * 
 * BigDecimal reward = policy.rewardFor("Groceries", new BigDecimal("100.00"));
 * // reward = 3.00
 * </pre>
 * 
 * <p>Category matching is case-insensitive and whitespace-trimmed.</p>
 * 
 * @author Brion Blais
 * @version 1.0
 * @since 2024-01-01
 */
public class RewardPolicy {
    
    /** Reward rate for grocery purchases (e.g., 0.03 for 3%) */
    private final BigDecimal groceriesRate;
    
    /** Reward rate for gas purchases (e.g., 0.02 for 2%) */
    private final BigDecimal gasRate;
    
    /** Reward rate for all other purchases (e.g., 0.01 for 1%) */
    private final BigDecimal otherRate;

    /**
     * Constructs a new RewardPolicy with specified rates for each category.
     * 
     * @param groceriesRate the reward rate for groceries (e.g., 0.03 for 3%), must not be null
     * @param gasRate the reward rate for gas (e.g., 0.02 for 2%), must not be null
     * @param otherRate the reward rate for all other categories (e.g., 0.01 for 1%), must not be null
     * @throws NullPointerException if any rate is null
     */
    public RewardPolicy(BigDecimal groceriesRate, BigDecimal gasRate, BigDecimal otherRate) {
        this.groceriesRate = groceriesRate;
        this.gasRate = gasRate;
        this.otherRate = otherRate;
    }

    /**
     * Returns a default reward policy with standard rates.
     * 
     * <p>Default rates are:</p>
     * <ul>
     *   <li>Groceries: 3%</li>
     *   <li>Gas: 2%</li>
     *   <li>Other: 1%</li>
     * </ul>
     * 
     * @return a new RewardPolicy with default rates, never null
     */
    public static RewardPolicy defaultPolicy() {
        return new RewardPolicy(new BigDecimal("0.03"), new BigDecimal("0.02"), new BigDecimal("0.01"));
    }

    /**
     * Calculates the reward amount for a transaction.
     * 
     * <p>The category is matched case-insensitively. Recognized categories are:</p>
     * <ul>
     *   <li>"groceries" - uses groceriesRate</li>
     *   <li>"gas" - uses gasRate</li>
     *   <li>Any other value (including null) - uses otherRate</li>
     * </ul>
     * 
     * <p>Leading/trailing whitespace is trimmed from the category.</p>
     * 
     * @param category the transaction category, case-insensitive, may be null
     * @param amount the transaction amount, must not be null
     * @return the calculated reward amount (amount * applicable rate), never null
     * @throws NullPointerException if amount is null
     */
    public BigDecimal rewardFor(String category, BigDecimal amount) {
        if (category == null) return amount.multiply(otherRate);
        String c = category.trim().toLowerCase(Locale.ROOT);
        switch (c) {
            case "groceries": return amount.multiply(groceriesRate);
            case "gas": return amount.multiply(gasRate);
            default: return amount.multiply(otherRate);
        }
    }
}