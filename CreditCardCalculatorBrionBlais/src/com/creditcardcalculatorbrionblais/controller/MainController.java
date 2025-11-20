package com.creditcardcalculatorbrionblais.controller;

import com.creditcardcalculatorbrionblais.model.transaction.*;
import com.creditcardcalculatorbrionblais.model.strategy.*;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Main controller for the credit card calculator application.
 * 
 * <p>This controller serves as the orchestration layer between the UI and the
 * business logic. It handles:</p>
 * <ul>
 *   <li>Loading transaction data from CSV files</li>
 *   <li>Configuring and running account simulations</li>
 *   <li>Coordinating between reward policies, payment strategies, and fee schedules</li>
 * </ul>
 * 
 * <p>Example usage:</p>
 * <pre>
 * MainController controller = new MainController();
 * List&lt;Transaction&gt; transactions = controller.loadCsv("transactions.csv");
 * 
 * AccountSimulator.Summary summary = controller.simulate(
 *     transactions,
 *     RewardPolicy.defaultPolicy(),
 *     new WallStreetTransactor(),
 *     AccountSimulator.InterestMethod.SYNCHRONY_DAILY_BALANCE,
 *     BigDecimal.ZERO,
 *     LocalDate.of(2024, 1, 1),
 *     LocalDate.of(2024, 12, 31)
 * );
 * </pre>
 * 
 * @author Brion Blais
 * @version 1.0
 * @since 2024-01-01
 */
public class MainController {
    
    /**
     * Loads transactions from a CSV file.
     * 
     * <p>The CSV file should contain one transaction per line in the format:</p>
     * <pre>yyyy-MM-dd,Category,Amount</pre>
     * 
     * <p>Example:</p>
     * <pre>
     * 2024-01-15,Groceries,125.50
     * 2024-01-16,Gas,45.00
     * </pre>
     * 
     * @param path the file path to the CSV file, must not be null
     * @return a list of Transaction objects parsed from the file, never null
     * @throws IOException if the file cannot be read or contains invalid data
     * @throws NullPointerException if path is null
     * @see TransactionReader#readFromCsv(java.io.Reader)
     */
    public List<Transaction> loadCsv(String path) throws IOException {
        try (FileReader fr = new FileReader(path)) {
            return TransactionReader.readFromCsv(fr);
        }
    }

    /**
     * Runs a credit card account simulation with the specified parameters.
     * 
     * <p>This method configures an AccountSimulator with all necessary components
     * and executes the simulation across the specified date range.</p>
     * 
     * <p>The simulation includes:</p>
     * <ul>
     *   <li>Transaction processing and categorization</li>
     *   <li>Reward calculation based on the reward policy</li>
     *   <li>Interest calculation using the specified method</li>
     *   <li>Fee application (paper statement, late fees, etc.)</li>
     *   <li>Payment processing according to the payment strategy</li>
     * </ul>
     * 
     * @param txs the list of transactions to simulate, must not be null
     * @param rewardPolicy the policy for calculating rewards, must not be null
     * @param paymentStrategy the strategy for determining payments, must not be null
     * @param method the interest calculation method (SYNCHRONY_DAILY_BALANCE or AVERAGE_DAILY_BALANCE)
     * @param startingBalance the initial account balance at the start date
     * @param startDate the first date of the simulation period, must not be null
     * @param endDate the last date of the simulation period, must not be null and must not be before startDate
     * @return a Summary containing all simulation results including balances, interest, fees, and time series data
     * @throws NullPointerException if any parameter is null
     * @throws IllegalArgumentException if endDate is before startDate
     * @see AccountSimulator#run()
     */
    public AccountSimulator.Summary simulate(List<Transaction> txs,
                                             RewardPolicy rewardPolicy,
                                             PaymentStrategy paymentStrategy,
                                             AccountSimulator.InterestMethod method,
                                             BigDecimal startingBalance,
                                             LocalDate startDate,
                                             LocalDate endDate) {
        FeeSchedule fees = new FeeSchedule();
        AccountSimulator sim = new AccountSimulator(txs, rewardPolicy, fees, paymentStrategy, method, startingBalance, startDate, endDate);
        return sim.run();
    }
}