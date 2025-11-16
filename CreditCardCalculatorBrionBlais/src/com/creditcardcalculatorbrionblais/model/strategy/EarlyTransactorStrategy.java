package com.creditcardcalculatorbrionblais.model.strategy;

import com.creditcardcalculatorbrionblais.model.account.Account;
import com.creditcardcalculatorbrionblais.model.transaction.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * Simplified: always schedule a payment equal to the current balance on the first day after cycle end.
 */
public class EarlyTransactorStrategy implements PaymentStrategy {

    @Override
    public List<Transaction> generatePayments(Account account, YearMonth statementPeriod) {
        // find approximate balance: sum of transactions up to end of month
        BigDecimal bal = account.getStartingBalance();
        for (var t : account.getTransactions()) {
            if (!t.getDate().isAfter(statementPeriod.atEndOfMonth())) {
                if (t.isPayment()) bal = bal.subtract(t.getAmount());
                else bal = bal.add(t.getAmount());
            }
        }
        // schedule payment one day after cycle end
        LocalDate payDate = statementPeriod.atEndOfMonth().plusDays(1);
        Transaction p = new Transaction(payDate, Transaction.Category.PAYMENT, bal.max(BigDecimal.ZERO));
        List<Transaction> out = new ArrayList<>();
        if (bal.signum() > 0) out.add(p);
        return out;
    }
}
