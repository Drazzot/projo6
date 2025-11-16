package com.creditcardcalculatorbrionblais.tests;

import com.creditcardcalculatorbrionblais.model.account.Account;
import com.creditcardcalculatorbrionblais.model.strategy.SynchronyDailyBalanceStrategy;
import com.creditcardcalculatorbrionblais.model.strategy.EarlyTransactorStrategy;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AccountBasicTest {

    @Test
    public void smokeSimulate() {
        Account a = new Account();
        a.setStartingBalance(BigDecimal.valueOf(1000));
        var r = a.simulate(new EarlyTransactorStrategy(), new SynchronyDailyBalanceStrategy());
        assertNotNull(r);
    }
}
