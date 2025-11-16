package com.creditcardcalculatorbrionblais.util;

import com.creditcardcalculatorbrionblais.model.transaction.Transaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple CSV loader:
 * yyyy-MM-dd,Category,amount
 */
public class CsvLoader {

    public static List<Transaction> load(Path path) throws IOException {
        List<Transaction> out = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split(",");
                if (parts.length < 3) continue;
                LocalDate date = LocalDate.parse(parts[0].trim());
                String cat = parts[1].trim().toUpperCase();
                Transaction.Category category;
                switch (cat) {
                    case "GROCERIES": category = Transaction.Category.GROCERIES; break;
                    case "GAS": category = Transaction.Category.GAS; break;
                    case "OTHER": category = Transaction.Category.OTHER; break;
                    case "PAYMENT": category = Transaction.Category.PAYMENT; break;
                    default: category = Transaction.Category.OTHER;
                }
                BigDecimal amount = new BigDecimal(parts[2].trim());
                out.add(new Transaction(date, category, amount));
            }
        }
        out.sort(null);
        return out;
    }
}
