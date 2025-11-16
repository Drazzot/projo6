package com.creditcardcalculatorbrionblais.model.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Immutable Transaction class
 */
public final class Transaction implements Comparable<Transaction> {
    public enum Category { GROCERIES, GAS, OTHER, PAYMENT }

    private final LocalDate date;
    private final Category category;
    private final BigDecimal amount; // positive for purchases; positive for payments (use isPayment flag)

    public Transaction(LocalDate date, Category category, BigDecimal amount) {
        Objects.requireNonNull(date);
        Objects.requireNonNull(category);
        Objects.requireNonNull(amount);
        this.date = date;
        this.category = category;
        this.amount = amount.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public LocalDate getDate() { return date; }
    public Category getCategory() { return category; }
    public BigDecimal getAmount() { return amount; }

    public boolean isPayment() { return category == Category.PAYMENT || amount.signum() < 0; }

    @Override
    public int compareTo(Transaction o) {
        return this.date.compareTo(o.date);
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", date, category, amount.toPlainString());
    }
}
