package com.creditcardcalculatorbrionblais.tests;

import com.creditcardcalculatorbrionblais.model.strategy.*;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for all PaymentStrategy implementations.
 * Tests EarlyTransactor, WallStreetTransactor, LightRevolver, and HeavyRevolver.
 */
public class PaymentStrategiesTest {
    
    private final LocalDate cycleStart = LocalDate.of(2024, 1, 1);
    private final LocalDate cycleEnd = LocalDate.of(2024, 1, 31);
    private final BigDecimal balance = new BigDecimal("1000.00");
    
    // EarlyTransactor Tests
    @Test
    public void testEarlyTransactorPaysFull() {
        PaymentStrategy strategy = new EarlyTransactor();
        PaymentInstruction instruction = strategy.nextPayment(balance, cycleStart, cycleEnd);
        
        assertEquals(balance, instruction.getAmount());
    }
    
    @Test
    public void testEarlyTransactorPaymentDate() {
        PaymentStrategy strategy = new EarlyTransactor();
        PaymentInstruction instruction = strategy.nextPayment(balance, cycleStart, cycleEnd);
        
        assertEquals(cycleEnd.plusDays(1), instruction.getDate());
    }
    
    @Test
    public void testEarlyTransactorZeroBalance() {
        PaymentStrategy strategy = new EarlyTransactor();
        PaymentInstruction instruction = strategy.nextPayment(BigDecimal.ZERO, cycleStart, cycleEnd);
        
        assertEquals(BigDecimal.ZERO, instruction.getAmount());
    }
    
    // WallStreetTransactor Tests
    @Test
    public void testWallStreetTransactorPaysFull() {
        PaymentStrategy strategy = new WallStreetTransactor();
        PaymentInstruction instruction = strategy.nextPayment(balance, cycleStart, cycleEnd);
        
        assertEquals(balance, instruction.getAmount());
    }
    
    @Test
    public void testWallStreetTransactorPaymentDate() {
        PaymentStrategy strategy = new WallStreetTransactor();
        PaymentInstruction instruction = strategy.nextPayment(balance, cycleStart, cycleEnd);
        
        assertEquals(cycleEnd.plusDays(22), instruction.getDate());
    }
    
    @Test
    public void testWallStreetTransactorLargeBalance() {
        PaymentStrategy strategy = new WallStreetTransactor();
        BigDecimal largeBalance = new BigDecimal("50000.00");
        PaymentInstruction instruction = strategy.nextPayment(largeBalance, cycleStart, cycleEnd);
        
        assertEquals(largeBalance, instruction.getAmount());
    }
    
    // LightRevolver Tests
    @Test
    public void testLightRevolverMinimumPayments() {
        PaymentStrategy strategy = new LightRevolver(0);
        
        // First 5 cycles should pay minimum
        for (int i = 0; i < 5; i++) {
            PaymentInstruction instruction = strategy.nextPayment(balance, cycleStart, cycleEnd);
            BigDecimal expected = balance.multiply(new BigDecimal("0.035")).max(new BigDecimal("30.00"));
            assertEquals(expected, instruction.getAmount());
        }
    }
    
    @Test
    public void testLightRevolverFullPaymentOn6thCycle() {
        PaymentStrategy strategy = new LightRevolver(0);
        
        // Skip first 5 cycles
        for (int i = 0; i < 5; i++) {
            strategy.nextPayment(balance, cycleStart, cycleEnd);
        }
        
        // 6th cycle should pay full
        PaymentInstruction instruction = strategy.nextPayment(balance, cycleStart, cycleEnd);
        assertEquals(balance, instruction.getAmount());
    }
    
    @Test
    public void testLightRevolverCycleRepeats() {
        PaymentStrategy strategy = new LightRevolver(0);
        
        // Complete one full cycle (6 payments)
        for (int i = 0; i < 6; i++) {
            strategy.nextPayment(balance, cycleStart, cycleEnd);
        }
        
        // 7th payment should be minimum again
        PaymentInstruction instruction = strategy.nextPayment(balance, cycleStart, cycleEnd);
        BigDecimal expected = balance.multiply(new BigDecimal("0.035")).max(new BigDecimal("30.00"));
        assertEquals(expected, instruction.getAmount());
    }
    
    @Test
    public void testLightRevolverPaymentDate() {
        PaymentStrategy strategy = new LightRevolver(0);
        PaymentInstruction instruction = strategy.nextPayment(balance, cycleStart, cycleEnd);
        
        assertEquals(cycleEnd.plusDays(22), instruction.getDate());
    }
    
    @Test
    public void testLightRevolverMinimumFloor() {
        PaymentStrategy strategy = new LightRevolver(0);
        BigDecimal smallBalance = new BigDecimal("100.00");
        PaymentInstruction instruction = strategy.nextPayment(smallBalance, cycleStart, cycleEnd);
        
        // 3.5% of 100 = 3.50, but minimum is 30.00
        assertEquals(new BigDecimal("30.00"), instruction.getAmount());
    }
    
    @Test
    public void testLightRevolverWithStartIndex() {
        PaymentStrategy strategy = new LightRevolver(5);
        
        // Starting at index 5 means first payment should be full
        PaymentInstruction instruction = strategy.nextPayment(balance, cycleStart, cycleEnd);
        assertEquals(balance, instruction.getAmount());
    }
    
    // HeavyRevolver Tests
    @Test
    public void testHeavyRevolverMinimumPayment() {
        PaymentStrategy strategy = new HeavyRevolver(0, 6);
        PaymentInstruction instruction = strategy.nextPayment(balance, cycleStart, cycleEnd);
        
        BigDecimal expected = balance.multiply(new BigDecimal("0.035")).max(new BigDecimal("30.00"));
        assertEquals(expected, instruction.getAmount());
    }
    
    @Test
    public void testHeavyRevolverOnTimePaymentDate() {
        PaymentStrategy strategy = new HeavyRevolver(0, 6);
        PaymentInstruction instruction = strategy.nextPayment(balance, cycleStart, cycleEnd);
        
        assertEquals(cycleEnd.plusDays(22), instruction.getDate());
    }
    
    @Test
    public void testHeavyRevolverLatePayment() {
        PaymentStrategy strategy = new HeavyRevolver(0, 6);
        
        // Skip to 6th cycle (index 5) which should be late
        for (int i = 0; i < 5; i++) {
            strategy.nextPayment(balance, cycleStart, cycleEnd);
        }
        
        PaymentInstruction instruction = strategy.nextPayment(balance, cycleStart, cycleEnd);
        assertEquals(cycleEnd.plusDays(30), instruction.getDate());
    }
    
    @Test
    public void testHeavyRevolverLateEvery3Cycles() {
        PaymentStrategy strategy = new HeavyRevolver(0, 3);
        
        // First 2 should be on time
        for (int i = 0; i < 2; i++) {
            PaymentInstruction instruction = strategy.nextPayment(balance, cycleStart, cycleEnd);
            assertEquals(cycleEnd.plusDays(22), instruction.getDate());
        }
        
        // 3rd should be late
        PaymentInstruction instruction = strategy.nextPayment(balance, cycleStart, cycleEnd);
        assertEquals(cycleEnd.plusDays(30), instruction.getDate());
    }
    
    @Test
    public void testHeavyRevolverMinimumFloor() {
        PaymentStrategy strategy = new HeavyRevolver(0, 6);
        BigDecimal smallBalance = new BigDecimal("50.00");
        PaymentInstruction instruction = strategy.nextPayment(smallBalance, cycleStart, cycleEnd);
        
        assertEquals(new BigDecimal("30.00"), instruction.getAmount());
    }
    
    @Test
    public void testHeavyRevolverWithStartIndex() {
        PaymentStrategy strategy = new HeavyRevolver(5, 6);
        
        // Starting at index 5 means first payment should be late
        PaymentInstruction instruction = strategy.nextPayment(balance, cycleStart, cycleEnd);
        assertEquals(cycleEnd.plusDays(30), instruction.getDate());
    }
    
    @Test
    public void testHeavyRevolverNeverLate() {
        PaymentStrategy strategy = new HeavyRevolver(0, 0);
        
        // With lateEveryNthCycle = 0, should never be late
        for (int i = 0; i < 10; i++) {
            PaymentInstruction instruction = strategy.nextPayment(balance, cycleStart, cycleEnd);
            assertEquals(cycleEnd.plusDays(22), instruction.getDate());
        }
    }
}