package com.creditcardcalculatorbrionblais.tests;

import com.creditcardcalculatorbrionblais.model.transaction.RewardPolicy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for RewardPolicy.
 * Validates reward calculations for different categories.
 */
public class RewardPolicyTest {
    
    private RewardPolicy policy;
    
    @BeforeEach
    public void setUp() {
        policy = new RewardPolicy(
            new BigDecimal("0.03"),  // 3% groceries
            new BigDecimal("0.02"),  // 2% gas
            new BigDecimal("0.01")   // 1% other
        );
    }
    
    @Test
    public void testGroceriesReward() {
        BigDecimal amount = new BigDecimal("100.00");
        BigDecimal reward = policy.rewardFor("Groceries", amount);
        
        assertEquals(new BigDecimal("3.0000"), reward);
    }
    
    @Test
    public void testGasReward() {
        BigDecimal amount = new BigDecimal("50.00");
        BigDecimal reward = policy.rewardFor("Gas", amount);
        
        assertEquals(new BigDecimal("1.0000"), reward);
    }
    
    @Test
    public void testOtherReward() {
        BigDecimal amount = new BigDecimal("200.00");
        BigDecimal reward = policy.rewardFor("Other", amount);
        
        assertEquals(new BigDecimal("2.0000"), reward);
    }
    
    @Test
    public void testCaseInsensitiveGroceries() {
        BigDecimal amount = new BigDecimal("100.00");
        
        assertEquals(policy.rewardFor("groceries", amount), 
                     policy.rewardFor("GROCERIES", amount));
        assertEquals(policy.rewardFor("Groceries", amount),
                     policy.rewardFor("  groceries  ", amount));
    }
    
    @Test
    public void testCaseInsensitiveGas() {
        BigDecimal amount = new BigDecimal("50.00");
        
        assertEquals(policy.rewardFor("gas", amount),
                     policy.rewardFor("GAS", amount));
    }
    
    @Test
    public void testNullCategoryUsesOtherRate() {
        BigDecimal amount = new BigDecimal("100.00");
        BigDecimal reward = policy.rewardFor(null, amount);
        
        assertEquals(new BigDecimal("1.0000"), reward);
    }
    
    @Test
    public void testUnknownCategoryUsesOtherRate() {
        BigDecimal amount = new BigDecimal("100.00");
        BigDecimal reward = policy.rewardFor("Electronics", amount);
        
        assertEquals(new BigDecimal("1.0000"), reward);
    }
    
    @Test
    public void testDefaultPolicy() {
        RewardPolicy defaultPolicy = RewardPolicy.defaultPolicy();
        
        BigDecimal groceriesReward = defaultPolicy.rewardFor("Groceries", new BigDecimal("100"));
        BigDecimal gasReward = defaultPolicy.rewardFor("Gas", new BigDecimal("100"));
        BigDecimal otherReward = defaultPolicy.rewardFor("Other", new BigDecimal("100"));
        
        assertEquals(new BigDecimal("3.00"), groceriesReward);
        assertEquals(new BigDecimal("2.00"), gasReward);
        assertEquals(new BigDecimal("1.00"), otherReward);
    }
    
    @Test
    public void testZeroAmount() {
        BigDecimal reward = policy.rewardFor("Groceries", new BigDecimal("0.00"));
        
        assertEquals(new BigDecimal("0.0000"), reward);
    }
    
    @Test
    public void testSmallAmount() {
        BigDecimal amount = new BigDecimal("0.01");
        BigDecimal reward = policy.rewardFor("Groceries", amount);
        
        assertEquals(new BigDecimal("0.0003"), reward);
    }
    
    @Test
    public void testLargeAmount() {
        BigDecimal amount = new BigDecimal("10000.00");
        BigDecimal reward = policy.rewardFor("Groceries", amount);
        
        assertEquals(new BigDecimal("300.0000"), reward);
    }
    
    @Test
    public void testCustomRates() {
        RewardPolicy customPolicy = new RewardPolicy(
            new BigDecimal("0.05"),
            new BigDecimal("0.03"),
            new BigDecimal("0.01")
        );
        
        BigDecimal reward = customPolicy.rewardFor("Groceries", new BigDecimal("100"));
        assertEquals(new BigDecimal("5.00"), reward);
    }
}