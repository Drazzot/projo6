package com.creditcardcalculatorbrionblais.controller;

import com.creditcardcalculatorbrionblais.model.account.Account;
import com.creditcardcalculatorbrionblais.model.account.SimulationResult;
import com.creditcardcalculatorbrionblais.model.strategy.EarlyTransactorStrategy;
import com.creditcardcalculatorbrionblais.model.strategy.InterestCalculationStrategy;
import com.creditcardcalculatorbrionblais.model.strategy.SynchronyDailyBalanceStrategy;
import com.creditcardcalculatorbrionblais.model.strategy.WallStreetTransactorStrategy;
import com.creditcardcalculatorbrionblais.model.strategy.PaymentStrategy;
import com.creditcardcalculatorbrionblais.util.CsvLoader;
import com.creditcardcalculatorbrionblais.view.MainWindow;

import java.nio.file.Path;
import java.util.List;

/**
 * Very small controller that wires up the UI and model.
 */
public class MainWindowController {

    private final MainWindow view;
    private final Account account;

    public MainWindowController() {
        this.view = new MainWindow(this);
        this.account = new Account();
    }

    public void start() {
        view.show();
    }

    public void loadCsv(Path p) {
        try {
            List<com.creditcardcalculatorbrionblais.model.transaction.Transaction> txs = CsvLoader.load(p);
            account.addAll(txs);
            view.setStatus("Loaded " + txs.size() + " transactions from " + p.getFileName());
        } catch (Exception e) {
            view.showError("Failed to load CSV: " + e.getMessage());
        }
    }

    public void runSimulation(String strategyName) {
        PaymentStrategy ps;
        if ("Early".equalsIgnoreCase(strategyName)) {
            ps = new EarlyTransactorStrategy();
        } else {
            ps = new WallStreetTransactorStrategy(20);
        }
        InterestCalculationStrategy is = new SynchronyDailyBalanceStrategy();
        SimulationResult result = account.simulate(ps, is);
        view.displayResult(result);
    }
}
