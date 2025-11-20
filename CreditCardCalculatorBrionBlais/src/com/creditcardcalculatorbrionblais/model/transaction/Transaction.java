package com.creditcardcalculatorbrionblais.model.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class Transaction {
    private final LocalDate date;
    private final String category; // Groceries, Gas, Other
    private final BigDecimal amount; // positive for purchases

    public Transaction(LocalDate date, String category, BigDecimal amount) {
        this.date = date;
        this.category = category;
        this.amount = amount;
    }

    public LocalDate getDate() { return date; }
    public String getCategory() { return category; }
    public BigDecimal getAmount() { return amount; }

    @Override
    public String toString() {
        return "Transaction{" + date + ", " + category + ", " + amount + "}";
    }
}
