package com.creditcardcalculatorbrionblais.model.strategy;

import com.creditcardcalculatorbrionblais.model.account.Account;
import com.creditcardcalculatorbrionblais.model.transaction.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * Pay full statement on due date (assumed day 20)
 */
public class WallStreetTransactorStrategy implements PaymentStrategy {

    private final int dueDay;

    public WallStreetTransactorStrategy(int dueDay) {
        this.dueDay = dueDay;
    }

    @Override
    public List<Transaction> generatePayments(Account account, YearMonth statementPeriod) {
        BigDecimal bal = account.getStartingBalance();
        for (var t : account.getTransactions()) {
            if (!t.getDate().isAfter(statementPeriod.atEndOfMonth())) {
                if (t.isPayment()) bal = bal.subtract(t.getAmount());
                else bal = bal.add(t.getAmount());
            }
        }
        LocalDate due = LocalDate.of(statementPeriod.getYear(), statementPeriod.getMonthValue(), Math.min(dueDay, statementPeriod.lengthOfMonth()));
        Transaction p = new Transaction(due, Transaction.Category.PAYMENT, bal.max(BigDecimal.ZERO));
        List<Transaction> out = new ArrayList<>();
        if (bal.signum() > 0) out.add(p);
        return out;
    }
}
