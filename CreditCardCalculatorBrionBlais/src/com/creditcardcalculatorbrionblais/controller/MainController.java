package com.creditcardcalculatorbrionblais.controller;

import com.creditcardcalculatorbrionblais.model.transaction.*;
import com.creditcardcalculatorbrionblais.model.strategy.*;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller glue: loads transactions, builds simulator using settings and runs it.
 */
public class MainController {
    public List<Transaction> loadCsv(String path) throws IOException {
        try (FileReader fr = new FileReader(path)) {
            return TransactionReader.readFromCsv(fr);
        }
    }

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