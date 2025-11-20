package com.creditcardcalculatorbrionblais.model.account;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public final class SimulationResult {

    public final BigDecimal totalInterest;
    public final BigDecimal totalRewards;
    public final BigDecimal totalFees;
    public final BigDecimal totalPayments;
    public final BigDecimal endingBalance;

    public final List<String> log;

    // Time-series fields for charts
    public final List<LocalDate> dates;
    public final List<BigDecimal> balances;
    public final List<BigDecimal> interestSeries;
    public final List<BigDecimal> feeSeries;
    public final List<BigDecimal> paymentSeries;

    public SimulationResult(
            BigDecimal totalInterest,
            BigDecimal totalRewards,
            BigDecimal totalFees,
            BigDecimal totalPayments,
            BigDecimal endingBalance,
            List<String> log,
            List<LocalDate> dates,
            List<BigDecimal> balances,
            List<BigDecimal> interestSeries,
            List<BigDecimal> feeSeries,
            List<BigDecimal> paymentSeries) {

        this.totalInterest = totalInterest;
        this.totalRewards = totalRewards;
        this.totalFees = totalFees;
        this.totalPayments = totalPayments;
        this.endingBalance = endingBalance;

        this.log = log;

        this.dates = dates;
        this.balances = balances;
        this.interestSeries = interestSeries;
        this.feeSeries = feeSeries;
        this.paymentSeries = paymentSeries;
    }
}
