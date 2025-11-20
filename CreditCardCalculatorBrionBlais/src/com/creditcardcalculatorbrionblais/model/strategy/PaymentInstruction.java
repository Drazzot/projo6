package com.creditcardcalculatorbrionblais.model.strategy;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Simple instruction: amount and date to apply as payment.
 */
public class PaymentInstruction {
    private final BigDecimal amount;
    private final LocalDate date;

    public PaymentInstruction(BigDecimal amount, LocalDate date) {
        this.amount = amount;
        this.date = date;
    }

    public BigDecimal getAmount() { return amount; }
    public LocalDate getDate() { return date; }
}