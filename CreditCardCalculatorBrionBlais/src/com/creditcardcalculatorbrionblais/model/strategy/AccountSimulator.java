package com.creditcardcalculatorbrionblais.model.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import com.creditcardcalculatorbrionblais.model.transaction.*;

/**
 * Simulation engine supporting:
 * - Synchrony daily balance interest calculation (daily rate = APR / 365)
 * - Minimum interest charge enforcement
 * - Fees (paper statement, late/returned) and promotional fee
 * - Rewards accumulation using RewardPolicy
 *
 * This is a simplified but faithful implementation of daily-balance described in the UseCaseDescription and Synchrony file.
 */
public class AccountSimulator {
    public enum InterestMethod { SYNCHRONY_DAILY_BALANCE, AVERAGE_DAILY_BALANCE }

    private final List<Transaction> transactions;
    private final RewardPolicy rewardPolicy;
    private final FeeSchedule fees;
    private final PaymentStrategy paymentStrategy;
    private final InterestMethod interestMethod;
    private final BigDecimal startingBalance;
    private final LocalDate startDate;
    private final LocalDate endDate;
    
    public static class Summary {
        public BigDecimal beginningBalance = BigDecimal.ZERO;
        public BigDecimal endingBalance = BigDecimal.ZERO;
        public BigDecimal totalPayments = BigDecimal.ZERO;
        public BigDecimal totalInterest = BigDecimal.ZERO;
        public BigDecimal totalFees = BigDecimal.ZERO;
        public BigDecimal totalRewards = BigDecimal.ZERO;
        
        public List<LocalDate> dates = new ArrayList<>();
        public List<BigDecimal> balances = new ArrayList<>();
        public List<BigDecimal> interestSeries = new ArrayList<>();
        public List<BigDecimal> feeSeries = new ArrayList<>();
        public List<BigDecimal> paymentSeries = new ArrayList<>();

    }

    public AccountSimulator(List<Transaction> transactions,
                            RewardPolicy rewardPolicy,
                            FeeSchedule fees,
                            PaymentStrategy paymentStrategy,
                            InterestMethod interestMethod,
                            BigDecimal startingBalance,
                            LocalDate startDate,
                            LocalDate endDate) {
        this.transactions = new ArrayList<>(transactions);
        this.rewardPolicy = rewardPolicy;
        this.fees = fees;
        this.paymentStrategy = paymentStrategy;
        this.interestMethod = interestMethod;
        this.startingBalance = startingBalance == null ? BigDecimal.ZERO : startingBalance;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Runs a month-by-month simulation from startDate to endDate inclusive. For brevity this implementation:
     * - groups transactions into monthly cycles (calendar month)
     * - uses simplified handling of fees and payments
     */
    public Summary run() {
        Summary s = new Summary();
        s.beginningBalance = startingBalance;
        BigDecimal currentBalance = startingBalance.setScale(2, RoundingMode.HALF_UP);
        s.totalRewards = BigDecimal.ZERO;
        s.totalFees = BigDecimal.ZERO;
        s.totalInterest = BigDecimal.ZERO;
        s.totalPayments = BigDecimal.ZERO;

        LocalDate cursor = LocalDate.of(startDate.getYear(), startDate.getMonth(), 1);
        LocalDate finalMonth = LocalDate.of(endDate.getYear(), endDate.getMonth(), 1);

        Map<LocalDate, List<Transaction>> byMonth = transactions.stream()
                .filter(t -> !t.getDate().isBefore(startDate) && !t.getDate().isAfter(endDate))
                .collect(Collectors.groupingBy(t -> LocalDate.of(t.getDate().getYear(), t.getDate().getMonth(), 1)));

        while (!cursor.isAfter(finalMonth)) {
            LocalDate cycleStart = cursor;
            LocalDate cycleEnd = cursor.withDayOfMonth(cursor.lengthOfMonth());
            // add transactions
            List<Transaction> monthTx = byMonth.getOrDefault(cursor, Collections.emptyList());
            BigDecimal monthPurchases = BigDecimal.ZERO;
            for (Transaction tx : monthTx) {
                monthPurchases = monthPurchases.add(tx.getAmount());
                // accumulate rewards
                s.totalRewards = s.totalRewards.add(rewardPolicy.rewardFor(tx.getCategory(), tx.getAmount()));
                currentBalance = currentBalance.add(tx.getAmount());
            }

            // apply paper statement fee if balance > 2.50
            if (currentBalance.compareTo(new BigDecimal("2.50")) > 0) {
                s.totalFees = s.totalFees.add(FeeSchedule.PAPER_STATEMENT_FEE);
                currentBalance = currentBalance.add(FeeSchedule.PAPER_STATEMENT_FEE);
            }

            // calculate interest for the cycle using daily model
            BigDecimal interestForCycle = calculateInterestForMonth(currentBalance, cycleStart, cycleEnd, FeeSchedule.APR_PURCHASES);
            // enforce minimum interest charge if positive interest computed is < $2.00 and interest > 0
            if (interestForCycle.compareTo(BigDecimal.ZERO) > 0 && interestForCycle.compareTo(FeeSchedule.MIN_INTEREST_CHARGE) < 0) {
                interestForCycle = FeeSchedule.MIN_INTEREST_CHARGE;
            }
            interestForCycle = interestForCycle.setScale(2, RoundingMode.HALF_UP);
            s.totalInterest = s.totalInterest.add(interestForCycle);
            currentBalance = currentBalance.add(interestForCycle);



                    
            // determine payment for cycle using strategy
            PaymentInstruction pi = paymentStrategy.nextPayment(currentBalance, cycleStart, cycleEnd);
            BigDecimal payment = pi.getAmount().min(currentBalance); // cannot pay more than current balance
            // detect late: if payDate > cycleEnd.plusDays(23) treat as late and apply late fee
            LocalDate dueDate = cycleEnd.plusDays(23);
            if (pi.getDate().isAfter(dueDate)) {
                // apply late fee high for simplicity every late application
                s.totalFees = s.totalFees.add(FeeSchedule.LATE_FEE_HIGH);
                currentBalance = currentBalance.add(FeeSchedule.LATE_FEE_HIGH);
            }
            
            // Record data BEFORE payment (end of cycle)
            s.dates.add(cycleEnd);
            s.balances.add(currentBalance);
            s.interestSeries.add(interestForCycle);
            s.feeSeries.add(s.totalFees);  // total fees up to this point
            
            // apply payment
            currentBalance = currentBalance.subtract(payment);
            s.totalPayments = s.totalPayments.add(payment);
            
            // Record payment
            s.paymentSeries.add(payment);


            cursor = cursor.plusMonths(1);
        }

        s.endingBalance = currentBalance.setScale(2, RoundingMode.HALF_UP);
        // round aggregates
        s.totalFees = s.totalFees.setScale(2, RoundingMode.HALF_UP);
        s.totalInterest = s.totalInterest.setScale(2, RoundingMode.HALF_UP);
        s.totalPayments = s.totalPayments.setScale(2, RoundingMode.HALF_UP);
        s.totalRewards = s.totalRewards.setScale(2, RoundingMode.HALF_UP);
        return s;
    }

    private BigDecimal calculateInterestForMonth(BigDecimal balance, LocalDate cycleStart, LocalDate cycleEnd, BigDecimal apr) {
        // daily rate = APR / 365
        BigDecimal days = new BigDecimal(cycleEnd.getDayOfMonth());
        BigDecimal dailyRate = apr.divide(new BigDecimal("365"), 10, RoundingMode.HALF_UP);
        // For simplicity, assume balance unchanged during month (since full daily tracking requires per-transaction day-by-day addition)
        BigDecimal interest = balance.multiply(dailyRate).multiply(days);
        return interest.setScale(10, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
    }
}