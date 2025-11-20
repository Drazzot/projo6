package com.creditcardcalculatorbrionblais.tests;

import org.junit.jupiter.api.Test;

import com.creditcardcalculatorbrionblais.model.transaction.FeeSchedule;

import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for FeeSchedule.
 * Validates constant values and currency rounding functionality.
 */
public class FeeScheduleTest {
    
    @Test
    public void testAprPurchasesValue() {
        assertEquals(new BigDecimal("0.3499"), FeeSchedule.APR_PURCHASES);
    }
    
    @Test
    public void testAprPenaltyValue() {
        assertEquals(new BigDecimal("0.3999"), FeeSchedule.APR_PENALTY);
    }
    
    @Test
    public void testMinInterestChargeValue() {
        assertEquals(new BigDecimal("2.00"), FeeSchedule.MIN_INTEREST_CHARGE);
    }
    
    @Test
    public void testPaperStatementFeeValue() {
        assertEquals(new BigDecimal("1.99"), FeeSchedule.PAPER_STATEMENT_FEE);
    }
    
    @Test
    public void testPromotionalFeeRateValue() {
        assertEquals(new BigDecimal("0.02"), FeeSchedule.PROMOTIONAL_FEE_RATE);
    }
    
    @Test
    public void testLateFeeValues() {
        assertEquals(new BigDecimal("30.00"), FeeSchedule.LATE_FEE_LOW);
        assertEquals(new BigDecimal("41.00"), FeeSchedule.LATE_FEE_HIGH);
    }
    
    @Test
    public void testRoundCurrencyTwoDecimals() {
        BigDecimal value = new BigDecimal("123.456");
        BigDecimal rounded = FeeSchedule.roundCurrency(value);
        
        assertEquals(new BigDecimal("123.46"), rounded);
    }
    
    @Test
    public void testRoundCurrencyHalfUp() {
        BigDecimal value = new BigDecimal("10.125");
        BigDecimal rounded = FeeSchedule.roundCurrency(value);
        
        assertEquals(new BigDecimal("10.13"), rounded);
    }
    
    @Test
    public void testRoundCurrencyHalfUpEdgeCase() {
        BigDecimal value = new BigDecimal("10.115");
        BigDecimal rounded = FeeSchedule.roundCurrency(value);
        
        assertEquals(new BigDecimal("10.12"), rounded);
    }
    
    @Test
    public void testRoundCurrencyAlreadyRounded() {
        BigDecimal value = new BigDecimal("50.00");
        BigDecimal rounded = FeeSchedule.roundCurrency(value);
        
        assertEquals(new BigDecimal("50.00"), rounded);
    }
    
    @Test
    public void testRoundCurrencyNull() {
        BigDecimal rounded = FeeSchedule.roundCurrency(null);
        
        assertNull(rounded);
    }
    
    @Test
    public void testRoundCurrencyZero() {
        BigDecimal value = BigDecimal.ZERO;
        BigDecimal rounded = FeeSchedule.roundCurrency(value);
        
        assertEquals(new BigDecimal("0.00"), rounded);
    }
    
    @Test
    public void testRoundCurrencyNegative() {
        BigDecimal value = new BigDecimal("-25.678");
        BigDecimal rounded = FeeSchedule.roundCurrency(value);
        
        assertEquals(new BigDecimal("-25.68"), rounded);
    }
    
    @Test
    public void testRoundCurrencyVerySmall() {
        BigDecimal value = new BigDecimal("0.001");
        BigDecimal rounded = FeeSchedule.roundCurrency(value);
        
        assertEquals(new BigDecimal("0.00"), rounded);
    }
    
    @Test
    public void testRoundCurrencyLargeValue() {
        BigDecimal value = new BigDecimal("999999.999");
        BigDecimal rounded = FeeSchedule.roundCurrency(value);
        
        assertEquals(new BigDecimal("1000000.00"), rounded);
    }
}