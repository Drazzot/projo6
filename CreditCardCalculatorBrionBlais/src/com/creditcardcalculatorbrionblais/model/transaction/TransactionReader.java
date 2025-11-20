package com.creditcardcalculatorbrionblais.model.transaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple CSV reader expecting headerless rows:
 * yyyy-MM-dd,Category,Amount
 * Category must be Groceries, Gas, or Other
 */
public class TransactionReader {
    public static List<Transaction> readFromCsv(Reader reader) throws IOException {
        List<Transaction> transactions = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(reader)) {
            String line;
            int lineNo = 0;
            while ((line = br.readLine()) != null) {
                lineNo++;
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] cols = line.split(",");
                if (cols.length < 3) throw new IOException("Malformed CSV at line " + lineNo);
                String dateStr = cols[0].trim();
                String cat = cols[1].trim();
                String amountStr = cols[2].trim().replace("$", "");
                try {
                    LocalDate date = LocalDate.parse(dateStr);
                    BigDecimal amt = new BigDecimal(amountStr);
                    transactions.add(new Transaction(date, cat, amt));
                } catch (DateTimeParseException | NumberFormatException e) {
                    throw new IOException("Invalid data at line " + lineNo + ": " + e.getMessage(), e);
                }
            }
        }
        return transactions;
    }
}