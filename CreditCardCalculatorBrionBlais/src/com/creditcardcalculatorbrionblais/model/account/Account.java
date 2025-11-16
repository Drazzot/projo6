package com.creditcardcalculatorbrionblais.model.account;

import com.creditcardcalculatorbrionblais.model.transaction.Transaction;
import com.creditcardcalculatorbrionblais.model.transaction.Transaction.Category;
import com.creditcardcalculatorbrionblais.model.strategy.InterestCalculationStrategy;
import com.creditcardcalculatorbrionblais.model.strategy.PaymentStrategy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Account model that holds transactions and runs simulations.
 */
public class Account implements Iterable<Transaction> {

    private final List<Transaction> transactions = new ArrayList<>();
    private BigDecimal startingBalance = BigDecimal.ZERO;
    private BigDecimal apr = BigDecimal.valueOf(0.35);
    private BigDecimal penaltyApr = BigDecimal.valueOf(0.3999);

    public Account() {}

    public void setStartingBalance(BigDecimal b) { this.startingBalance = b; }
    public BigDecimal getStartingBalance() { return startingBalance; }

    public void setApr(BigDecimal apr) { this.apr = apr; }
    public BigDecimal getApr() { return apr; }

    public void addTransaction(Transaction t) { transactions.add(t); transactions.sort(null); }

    public void addAll(List<Transaction> txs) { transactions.addAll(txs); transactions.sort(null); }

    public List<Transaction> getTransactions() { return List.copyOf(transactions); }

    /**
     * Very small simulation: run interest strategy over months between first and last transaction
     * and allow payment strategy to generate payments. This is intentionally simple but functional.
     */
    public SimulationResult simulate(PaymentStrategy paymentStrategy, InterestCalculationStrategy interestStrategy) {
        Objects.requireNonNull(paymentStrategy);
        Objects.requireNonNull(interestStrategy);

        if (transactions.isEmpty()) {
            return new SimulationResult(startingBalance, startingBalance);
        }

        LocalDate start = transactions.get(0).getDate().withDayOfMonth(1);
        LocalDate end = transactions.get(transactions.size()-1).getDate();
        LocalDate cursor = start;

        BigDecimal balance = startingBalance;
        BigDecimal totalInterest = BigDecimal.ZERO;
        BigDecimal totalFees = BigDecimal.ZERO;
        BigDecimal totalRewards = BigDecimal.ZERO;
        BigDecimal totalPayments = BigDecimal.ZERO;

        List<String> log = new ArrayList<>();

        while (!cursor.isAfter(end)) {
            YearMonth ym = YearMonth.from(cursor);
            LocalDate cycleStart = ym.atDay(1);
            LocalDate cycleEnd = ym.atEndOfMonth();
            // collect charges in this cycle
            List<Transaction> cycleTx = new ArrayList<>();
            for (Transaction t : transactions) {
                if (!t.getDate().isBefore(cycleStart) && !t.getDate().isAfter(cycleEnd)) {
                    cycleTx.add(t);
                }
            }
            // apply purchases (simplified): add sums to balance and compute rewards
            BigDecimal purchasesSum = BigDecimal.ZERO;
            BigDecimal rewardsThisCycle = BigDecimal.ZERO;
            for (Transaction t : cycleTx) {
                if (!t.isPayment()) {
                    purchasesSum = purchasesSum.add(t.getAmount());
                    switch (t.getCategory()) {
                        case GROCERIES: rewardsThisCycle = rewardsThisCycle.add(t.getAmount().multiply(java.math.BigDecimal.valueOf(0.03))); break;
                        case GAS: rewardsThisCycle = rewardsThisCycle.add(t.getAmount().multiply(java.math.BigDecimal.valueOf(0.02))); break;
                        default: rewardsThisCycle = rewardsThisCycle.add(t.getAmount().multiply(java.math.BigDecimal.valueOf(0.01))); break;
                    }
                } else {
                    // payments reduce balance immediately
                    balance = balance.subtract(t.getAmount());
                    totalPayments = totalPayments.add(t.getAmount());
                }
            }
            balance = balance.add(purchasesSum);
            totalRewards = totalRewards.add(rewardsThisCycle);

            // interest calculation
            var interestResult = interestStrategy.computeInterest(balance, apr, cycleStart, cycleEnd);
            BigDecimal interest = interestResult.totalInterest;
            balance = balance.add(interest);
            totalInterest = totalInterest.add(interest);
            log.add(String.format("%s purchases=%s rewards=%s interest=%s balance=%s", ym, purchasesSum, rewardsThisCycle.setScale(2, BigDecimal.ROUND_HALF_UP), interest.setScale(2, BigDecimal.ROUND_HALF_UP), balance.setScale(2, BigDecimal.ROUND_HALF_UP)));

            // let payment strategy schedule payments (simplified - one payment on due date)
            var payments = paymentStrategy.generatePayments(this, ym);
            for (Transaction p : payments) {
                // assume payments amount positive and reduce balance
                balance = balance.subtract(p.getAmount());
                totalPayments = totalPayments.add(p.getAmount());
                log.add("Payment: " + p);
            }

            cursor = cursor.plusMonths(1);
        }

        return new SimulationResult(totalInterest.setScale(2, BigDecimal.ROUND_HALF_UP), totalRewards.setScale(2, BigDecimal.ROUND_HALF_UP), totalFees.setScale(2, BigDecimal.ROUND_HALF_UP), totalPayments.setScale(2, BigDecimal.ROUND_HALF_UP), balance.setScale(2, BigDecimal.ROUND_HALF_UP), log);
    }

    @Override
    public Iterator<Transaction> iterator() {
        return List.copyOf(transactions).iterator();
    }
}
