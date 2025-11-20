package com.creditcardcalculatorbrionblais.tests;

import com.creditcardcalculatorbrionblais.model.strategy.*;
import com.creditcardcalculatorbrionblais.model.transaction.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for AccountSimulator.
 * Validates simulation logic, interest calculation, and fee application.
 */
public class AccountSimulatorTest {
    
    private List<Transaction> transactions;
    private RewardPolicy rewardPolicy;
    private FeeSchedule fees;
    private LocalDate startDate;
    private LocalDate endDate;
    
    @BeforeEach
    public void setUp() {
        transactions = new ArrayList<>();
        rewardPolicy = RewardPolicy.defaultPolicy();
        fees = new FeeSchedule();
        startDate = LocalDate.of(2024, 1, 1);
        endDate = LocalDate.of(2024, 1, 31);
    }
    
    @Test
    public void testSimulationWithNoTransactions() {
        AccountSimulator sim = new AccountSimulator(
            transactions,
            rewardPolicy,
            fees,
            new EarlyTransactor(),
            AccountSimulator.InterestMethod.SYNCHRONY_DAILY_BALANCE,
            BigDecimal.ZERO,
            startDate,
            endDate
        );
        
        AccountSimulator.Summary summary = sim.run();
        
        assertNotNull(summary);
        assertEquals(BigDecimal.ZERO, summary.beginningBalance);
        assertEquals(new BigDecimal("0.00"), summary.endingBalance);
    }
    
    @Test
    public void testSimulationWithSingleTransaction() {
        transactions.add(new Transaction(
            LocalDate.of(2024, 1, 15),
            "Groceries",
            new BigDecimal("100.00")
        ));
        
        AccountSimulator sim = new AccountSimulator(
            transactions,
            rewardPolicy,
            fees,
            new EarlyTransactor(),
            AccountSimulator.InterestMethod.SYNCHRONY_DAILY_BALANCE,
            BigDecimal.ZERO,
            startDate,
            endDate
        );
        
        AccountSimulator.Summary summary = sim.run();
        
        assertNotNull(summary);
        assertTrue(summary.totalRewards.compareTo(BigDecimal.ZERO) > 0);
        assertTrue(summary.totalPayments.compareTo(BigDecimal.ZERO) > 0);
    }
    
    @Test
    public void testRewardsAccumulation() {
        transactions.add(new Transaction(
            LocalDate.of(2024, 1, 15),
            "Groceries",
            new BigDecimal("100.00")
        ));
        
        AccountSimulator sim = new AccountSimulator(
            transactions,
            rewardPolicy,
            fees,
            new EarlyTransactor(),
            AccountSimulator.InterestMethod.SYNCHRONY_DAILY_BALANCE,
            BigDecimal.ZERO,
            startDate,
            endDate
        );
        
        AccountSimulator.Summary summary = sim.run();
        
        // 3% of 100 = 3.00
        assertEquals(new BigDecimal("3.00"), summary.totalRewards);
    }
    
    @Test
    public void testPaperStatementFeeApplied() {
        transactions.add(new Transaction(
            LocalDate.of(2024, 1, 15),
            "Gas",
            new BigDecimal("50.00")
        ));
        
        AccountSimulator sim = new AccountSimulator(
            transactions,
            rewardPolicy,
            fees,
            new WallStreetTransactor(),
            AccountSimulator.InterestMethod.SYNCHRONY_DAILY_BALANCE,
            BigDecimal.ZERO,
            startDate,
            endDate
        );
        
        AccountSimulator.Summary summary = sim.run();
        
        // Should include paper statement fee
        assertTrue(summary.totalFees.compareTo(BigDecimal.ZERO) > 0);
    }
    
    @Test
    public void testInterestAccrualWithRevolver() {
        transactions.add(new Transaction(
            LocalDate.of(2024, 1, 15),
            "Other",
            new BigDecimal("1000.00")
        ));
        
        AccountSimulator sim = new AccountSimulator(
            transactions,
            rewardPolicy,
            fees,
            new LightRevolver(0),
            AccountSimulator.InterestMethod.SYNCHRONY_DAILY_BALANCE,
            BigDecimal.ZERO,
            startDate,
            LocalDate.of(2024, 6, 30) // 6 months
        );
        
        AccountSimulator.Summary summary = sim.run();
        
        // Should accrue interest over 6 months
        assertTrue(summary.totalInterest.compareTo(BigDecimal.ZERO) > 0);
    }
    
    @Test
    public void testTimeSeriesDataPopulated() {
        transactions.add(new Transaction(
            LocalDate.of(2024, 1, 15),
            "Groceries",
            new BigDecimal("100.00")
        ));
        
        AccountSimulator sim = new AccountSimulator(
            transactions,
            rewardPolicy,
            fees,
            new EarlyTransactor(),
            AccountSimulator.InterestMethod.SYNCHRONY_DAILY_BALANCE,
            BigDecimal.ZERO,
            startDate,
            endDate
        );
        
        AccountSimulator.Summary summary = sim.run();
        
        assertFalse(summary.dates.isEmpty());
        assertFalse(summary.balances.isEmpty());
        assertEquals(summary.dates.size(), summary.balances.size());
    }
    
    @Test
    public void testStartingBalanceIsRespected() {
        BigDecimal startBalance = new BigDecimal("500.00");
        
        AccountSimulator sim = new AccountSimulator(
            transactions,
            rewardPolicy,
            fees,
            new EarlyTransactor(),
            AccountSimulator.InterestMethod.SYNCHRONY_DAILY_BALANCE,
            startBalance,
            startDate,
            endDate
        );
        
        AccountSimulator.Summary summary = sim.run();
        
        assertEquals(startBalance, summary.beginningBalance);
    }
    
    @Test
    public void testMultipleMonthSimulation() {
        for (int month = 1; month <= 3; month++) {
            transactions.add(new Transaction(
                LocalDate.of(2024, month, 15),
                "Groceries",
                new BigDecimal("200.00")
            ));
        }
        
        AccountSimulator sim = new AccountSimulator(
            transactions,
            rewardPolicy,
            fees,
            new WallStreetTransactor(),
            AccountSimulator.InterestMethod.SYNCHRONY_DAILY_BALANCE,
            BigDecimal.ZERO,
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 3, 31)
        );
        
        AccountSimulator.Summary summary = sim.run();
        
        assertTrue(summary.dates.size() >= 3);
        assertTrue(summary.totalRewards.compareTo(BigDecimal.ZERO) > 0);
    }
    
    @Test
    public void testAverageDailyBalanceMethod() {
        transactions.add(new Transaction(
            LocalDate.of(2024, 1, 15),
            "Gas",
            new BigDecimal("100.00")
        ));
        
        AccountSimulator sim = new AccountSimulator(
            transactions,
            rewardPolicy,
            fees,
            new WallStreetTransactor(),
            AccountSimulator.InterestMethod.AVERAGE_DAILY_BALANCE,
            BigDecimal.ZERO,
            startDate,
            endDate
        );
        
        AccountSimulator.Summary summary = sim.run();
        
        assertNotNull(summary);
    }
    
    @Test
    public void testMinimumInterestChargeEnforcement() {
        // Small balance should still incur minimum interest charge if revolved
        transactions.add(new Transaction(
            LocalDate.of(2024, 1, 15),
            "Other",
            new BigDecimal("10.00")
        ));
        
        AccountSimulator sim = new AccountSimulator(
            transactions,
            rewardPolicy,
            fees,
            new LightRevolver(0),
            AccountSimulator.InterestMethod.SYNCHRONY_DAILY_BALANCE,
            BigDecimal.ZERO,
            startDate,
            endDate
        );
        
        AccountSimulator.Summary summary = sim.run();
        
        // If interest accrued, it should be at least the minimum
        if (summary.totalInterest.compareTo(BigDecimal.ZERO) > 0) {
            assertTrue(summary.totalInterest.compareTo(FeeSchedule.MIN_INTEREST_CHARGE) >= 0);
        }
    }
}