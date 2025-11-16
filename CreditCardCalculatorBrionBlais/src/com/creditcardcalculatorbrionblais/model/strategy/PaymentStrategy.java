package com.creditcardcalculatorbrionblais.model.strategy;

import com.creditcardcalculatorbrionblais.model.account.Account;
import com.creditcardcalculatorbrionblais.model.transaction.Transaction;

import java.time.YearMonth;
import java.util.List;

public interface PaymentStrategy {
    /**
     * Given an account and a statement period, return list of payments to schedule.
     * Amounts should be positive numbers representing payments (they will be subtracted from balance).
     */
    List<Transaction> generatePayments(Account account, YearMonth statementPeriod);
}
