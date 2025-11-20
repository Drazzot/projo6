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
 * Utility class for reading transactions from CSV files.
 * 
 * <p>This reader expects CSV files with three columns per row (no header):</p>
 * <ol>
 *   <li>Date in yyyy-MM-dd format</li>
 *   <li>Category (e.g., Groceries, Gas, Other)</li>
 *   <li>Amount (numeric value, optionally prefixed with $)</li>
 * </ol>
 * 
 * <p>Example CSV format:</p>
 * <pre>
 * 2024-01-15,Groceries,125.50
 * 2024-01-16,Gas,$45.00
 * 2024-01-17,Other,99.99
 * </pre>
 * 
 * <p>Empty lines are skipped. Whitespace around values is trimmed.</p>
 * 
 * @author Brion Blais
 * @version 1.0
 * @since 2024-01-01
 */
public class TransactionReader {
    
    /**
     * Reads transactions from a CSV reader.
     * 
     * <p>Each line should contain: date,category,amount</p>
     * <ul>
     *   <li>Date must be in yyyy-MM-dd format</li>
     *   <li>Category is a string (Groceries, Gas, or Other recommended)</li>
     *   <li>Amount can optionally include a dollar sign ($)</li>
     * </ul>
     * 
     * <p>Empty lines are ignored. Malformed lines will cause an IOException.</p>
     * 
     * @param reader the Reader providing CSV data, must not be null
     * @return a list of Transaction objects parsed from the CSV, never null
     * @throws IOException if there is an error reading from the reader,
     *                     if a line is malformed (fewer than 3 columns),
     *                     if a date cannot be parsed,
     *                     or if an amount is not a valid number
     * @throws NullPointerException if reader is null
     */
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