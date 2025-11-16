package com.creditcardcalculatorbrionblais.model.strategy;

import java.math.BigDecimal;
import java.util.List;

public final class InterestResult {
    public final BigDecimal totalInterest;
    public final List<BigDecimal> dailyInterests;

    public InterestResult(BigDecimal totalInterest, List<BigDecimal> dailyInterests) {
        this.totalInterest = totalInterest;
        this.dailyInterests = dailyInterests;
    }
}
