package com.creditcardcalculatorbrionblais.model.account;

import java.math.BigDecimal;
import java.util.List;

public final class SimulationResult {
    public final BigDecimal totalInterest;
    public final BigDecimal totalRewards;
    public final BigDecimal totalFees;
    public final BigDecimal totalPayments;
    public final BigDecimal endingBalance;
    public final List<String> log;

    public SimulationResult(BigDecimal totalInterest, BigDecimal totalRewards, BigDecimal totalFees, BigDecimal totalPayments, BigDecimal endingBalance, List<String> log) {
        this.totalInterest = totalInterest;
        this.totalRewards = totalRewards;
        this.totalFees = totalFees;
        this.totalPayments = totalPayments;
        this.endingBalance = endingBalance;
        this.log = log;
    }

    // convenience constructor for empty
    public SimulationResult(BigDecimal opening, BigDecimal ending) {
        this.totalInterest = opening;
        this.totalRewards = ending;
        this.totalFees = java.math.BigDecimal.ZERO;
        this.totalPayments = java.math.BigDecimal.ZERO;
        this.endingBalance = ending;
        this.log = List.of();
    }
}
