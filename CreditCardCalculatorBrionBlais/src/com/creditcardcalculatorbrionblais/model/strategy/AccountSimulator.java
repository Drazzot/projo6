package com.creditcardcalculatorbrionblais.model.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import com.creditcardcalculatorbrionblais.model.transaction.*;

/**
 * Core simulation engine for credit card account behavior.
 * 
 * <p>This simulator implements a month-by-month account simulation that models:</p>
 * <ul>
 *   <li>Transaction processing and balance updates</li>
 *   <li>Interest calculation using either Synchrony daily balance or average daily balance methods</li>
 *   <li>Fee application (paper statement, late payment fees)</li>
 *   <li>Reward accumulation based on purchase categories</li>
 *   <li>Payment processing according to configurable strategies</li>
 * </ul>
 * 
 * <p>The simulation processes transactions chronologically, grouped by calendar month,
 * applying all relevant charges and credits to maintain an accurate running balance.</p>
 * 
 * <p>Example usage:</p>
 * <pre>
 * AccountSimulator sim = new AccountSimulator(
 *     transactions,
 *     RewardPolicy.defaultPolicy(),
 *     new FeeSchedule(),
 *     new WallStreetTransactor(),
 *     InterestMethod.SYNCHRONY_DAILY_BALANCE,
 *     BigDecimal.ZERO,
 *     LocalDate.of(2024, 1, 1),
 *     LocalDate.of(2024, 12, 31)
 * );
 * Summary summary = sim.run();
 * </pre>
 * 
 * @author Brion Blais
 * @version 1.0
 * @since 2024-01-01
 */
public class AccountSimulator {
    
    /**
     * Enumeration of supported interest calculation methods.
     */
    public enum InterestMethod { 
        /** Synchrony daily balance method: compounds interest daily */
        SYNCHRONY_DAILY_BALANCE, 
        
        /** Average daily balance method: calculates interest on average balance */
        AVERAGE_DAILY_BALANCE 
    }

    private final List<Transaction> transactions;
    private final RewardPolicy rewardPolicy;
    private final FeeSchedule fees;
    private final PaymentStrategy paymentStrategy;
    private final InterestMethod interestMethod;
    private final BigDecimal startingBalance;
    private final LocalDate startDate;
    private final LocalDate endDate;
    
    /**
     * Container for simulation results including totals and time-series data.
     * 
     * <p>This class holds all computed values from a simulation run, including:</p>
     * <ul>
     *   <li>Beginning and ending balances</li>
     *   <li>Total payments, interest, fees, and rewards</li>
     *   <li>Time-series data for visualization (dates, balances, interest, fees, payments)</li>
     * </ul>
     */
    public static class Summary {
        /** The balance at the start of the simulation */
        public BigDecimal beginningBalance = BigDecimal.ZERO;
        
        /** The balance at the end of the simulation */
        public BigDecimal endingBalance = BigDecimal.ZERO;
        
        /** Total amount paid during the simulation */
        public BigDecimal totalPayments = BigDecimal.ZERO;
        
        /** Total interest charged during the simulation */
        public BigDecimal totalInterest = BigDecimal.ZERO;
        
        /** Total fees charged during the simulation */
        public BigDecimal totalFees = BigDecimal.ZERO;
        
        /** Total rewards earned during the simulation */
        public BigDecimal totalRewards = BigDecimal.ZERO;
        
        /** List of dates for time-series visualization */
        public List<LocalDate> dates = new ArrayList<>();
        
        /** List of balances corresponding to each date */
        public List<BigDecimal> balances = new ArrayList<>();
        
        /** List of interest amounts for each period */
        public List<BigDecimal> interestSeries = new ArrayList<>();
        
        /** List of fee amounts for each period */
        public List<BigDecimal> feeSeries = new ArrayList<>();
        
        /** List of payment amounts for each period */
        public List<BigDecimal> paymentSeries = new ArrayList<>();
    }

    /**
     * Constructs a new AccountSimulator with all necessary configuration.
     * 
     * @param transactions the list of transactions to process, must not be null
     * @param rewardPolicy the policy for calculating rewards, must not be null
     * @param fees the fee schedule (currently unused but available for extension)
     * @param paymentStrategy the strategy for determining payments, must not be null
     * @param interestMethod the method for calculating interest, must not be null
     * @param startingBalance the initial balance, null is treated as zero
     * @param startDate the first date of simulation, must not be null
     * @param endDate the last date of simulation, must not be null
     */
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
     * Executes the simulation and returns a summary of results.
     * 
     * <p>The simulation proceeds month-by-month from startDate to endDate (inclusive),
     * processing transactions, calculating interest and fees, applying rewards, and
     * handling payments according to the configured strategy.</p>
     * 
     * <p>For each month, the simulation:</p>
     * <ol>
     *   <li>Applies all transactions occurring in that month</li>
     *   <li>Calculates and accumulates rewards</li>
     *   <li>Applies paper statement fee if balance &gt; $2.50</li>
     *   <li>Calculates interest for the month</li>
     *   <li>Enforces minimum interest charge ($2.00) if applicable</li>
     *   <li>Determines payment amount and date via payment strategy</li>
     *   <li>Applies late fee if payment is after due date</li>
     *   <li>Records time-series data for visualization</li>
     *   <li>Applies the payment to reduce balance</li>
     * </ol>
     * 
     * <p>All monetary values in the returned Summary are rounded to 2 decimal places.</p>
     * 
     * @return a Summary object containing all simulation results, never null
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

    /**
     * Calculates interest for a single month using simplified daily rate model.
     * 
     * <p>This is a simplified implementation that assumes the balance remains
     * constant throughout the month. The calculation is:</p>
     * <pre>
     * dailyRate = APR / 365
     * interest = balance * dailyRate * daysInMonth
     * </pre>
     * 
     * @param balance the balance for the month
     * @param cycleStart the first day of the month
     * @param cycleEnd the last day of the month
     * @param apr the annual percentage rate
     * @return the calculated interest amount for the month
     */
    private BigDecimal calculateInterestForMonth(BigDecimal balance, LocalDate cycleStart, LocalDate cycleEnd, BigDecimal apr) {
        // daily rate = APR / 365
        BigDecimal days = new BigDecimal(cycleEnd.getDayOfMonth());
        BigDecimal dailyRate = apr.divide(new BigDecimal("365"), 10, RoundingMode.HALF_UP);
        // For simplicity, assume balance unchanged during month (since full daily tracking requires per-transaction day-by-day addition)
        BigDecimal interest = balance.multiply(dailyRate).multiply(days);
        return interest.setScale(10, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
    }
}