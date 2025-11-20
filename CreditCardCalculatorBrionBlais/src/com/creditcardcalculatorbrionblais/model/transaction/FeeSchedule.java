package com.creditcardcalculatorbrionblais.model.transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Defines credit card fee schedule and APR constants.
 * 
 * <p>This class encapsulates all fee and interest rate constants used in
 * credit card calculations, derived from Synchrony Bank credit card terms.
 * All values are represented as immutable BigDecimal constants.</p>
 * 
 * <p>Key constants include:</p>
 * <ul>
 *   <li>Purchase and penalty APRs</li>
 *   <li>Minimum interest charge</li>
 *   <li>Paper statement fees</li>
 *   <li>Late payment fees (tiered)</li>
 *   <li>Promotional fees</li>
 * </ul>
 * 
 * @author Brion Blais
 * @version 1.0
 * @since 2024-01-01
 */
public class FeeSchedule {
    
    /** Annual Percentage Rate for purchases: 34.99% */
    public static final BigDecimal APR_PURCHASES = new BigDecimal("0.3499");
    
    /** Penalty APR for missed payments: 39.99% */
    public static final BigDecimal APR_PENALTY = new BigDecimal("0.3999");

    /** Minimum interest charge per billing cycle: $2.00 */
    public static final BigDecimal MIN_INTEREST_CHARGE = new BigDecimal("2.00");

    /** Monthly fee for paper statements: $1.99 */
    public static final BigDecimal PAPER_STATEMENT_FEE = new BigDecimal("1.99");

    /** 
     * Promotional fee rate for long-term promotions: 2%
     * Applied as a percentage of the promotional balance
     */
    public static final BigDecimal PROMOTIONAL_FEE_RATE = new BigDecimal("0.02");

    /** Late payment fee for smaller balances: $30.00 */
    public static final BigDecimal LATE_FEE_LOW = new BigDecimal("30.00");
    
    /** Late payment fee for larger balances: $41.00 */
    public static final BigDecimal LATE_FEE_HIGH = new BigDecimal("41.00");

    /**
     * Rounds a monetary value to two decimal places using HALF_UP rounding.
     * 
     * <p>This method ensures all currency values are properly formatted with
     * exactly two decimal places. For example:</p>
     * <ul>
     *   <li>123.456 becomes 123.46</li>
     *   <li>10.125 becomes 10.13</li>
     *   <li>50.1 becomes 50.10</li>
     * </ul>
     * 
     * @param v the value to round, may be null
     * @return the rounded value with 2 decimal places, or null if input is null
     */
    public static BigDecimal roundCurrency(BigDecimal v) {
        if (v == null) return null;
        return v.setScale(2, RoundingMode.HALF_UP);
    }
}