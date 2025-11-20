package com.creditcardcalculatorbrionblais.tests;

import org.junit.jupiter.api.Test;

import com.creditcardcalculatorbrionblais.model.transaction.Transaction;

import org.junit.jupiter.api.BeforeEach;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for the Transaction class.
 * Validates transaction creation, getters, and toString output.
 */
public class TransactionTest {
    
    private LocalDate testDate;
    private String testCategory;
    private BigDecimal testAmount;
    
    @BeforeEach
    public void setUp() {
        testDate = LocalDate.of(2024, 1, 15);
        testCategory = "Groceries";
        testAmount = new BigDecimal("125.50");
    }
    
    @Test
    public void testTransactionCreation() {
        Transaction tx = new Transaction(testDate, testCategory, testAmount);
        
        assertNotNull(tx);
        assertEquals(testDate, tx.getDate());
        assertEquals(testCategory, tx.getCategory());
        assertEquals(testAmount, tx.getAmount());
    }
    
    @Test
    public void testTransactionWithGasCategory() {
        Transaction tx = new Transaction(testDate, "Gas", new BigDecimal("45.00"));
        
        assertEquals("Gas", tx.getCategory());
        assertEquals(new BigDecimal("45.00"), tx.getAmount());
    }
    
    @Test
    public void testTransactionWithOtherCategory() {
        Transaction tx = new Transaction(testDate, "Other", new BigDecimal("99.99"));
        
        assertEquals("Other", tx.getCategory());
    }
    
    @Test
    public void testTransactionWithZeroAmount() {
        Transaction tx = new Transaction(testDate, testCategory, BigDecimal.ZERO);
        
        assertEquals(BigDecimal.ZERO, tx.getAmount());
    }
    
    @Test
    public void testTransactionWithLargeAmount() {
        BigDecimal largeAmount = new BigDecimal("9999999.99");
        Transaction tx = new Transaction(testDate, testCategory, largeAmount);
        
        assertEquals(largeAmount, tx.getAmount());
    }
    
    @Test
    public void testTransactionToString() {
        Transaction tx = new Transaction(testDate, testCategory, testAmount);
        String result = tx.toString();
        
        assertTrue(result.contains("2024-01-15"));
        assertTrue(result.contains("Groceries"));
        assertTrue(result.contains("125.50"));
    }
    
    @Test
    public void testTransactionImmutability() {
        Transaction tx = new Transaction(testDate, testCategory, testAmount);
        
        // Verify getters return same values
        assertEquals(testDate, tx.getDate());
        assertEquals(testCategory, tx.getCategory());
        assertEquals(testAmount, tx.getAmount());
    }
    
    @Test
    public void testTransactionWithNullCategory() {
        Transaction tx = new Transaction(testDate, null, testAmount);
        
        assertNull(tx.getCategory());
    }
    
    @Test
    public void testMultipleTransactionsSameDate() {
        Transaction tx1 = new Transaction(testDate, "Groceries", new BigDecimal("50.00"));
        Transaction tx2 = new Transaction(testDate, "Gas", new BigDecimal("75.00"));
        
        assertEquals(tx1.getDate(), tx2.getDate());
        assertNotEquals(tx1.getCategory(), tx2.getCategory());
    }
}