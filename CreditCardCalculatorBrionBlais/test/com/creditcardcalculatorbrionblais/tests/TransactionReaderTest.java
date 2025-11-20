package com.creditcardcalculatorbrionblais.tests;

import static org.junit.jupiter.api.Assertions.*;
import com.creditcardcalculatorbrionblais.model.transaction.*;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

public class TransactionReaderTest {

    @Test
    void testValidCsvParsesCorrectly() throws IOException {
        String csv = """
            2024-01-01,Groceries,123.45
            2024-01-02,Gas,50.00
            2024-01-03,Other,$19.99
            """;

        List<Transaction> result = TransactionReader.readFromCsv(new StringReader(csv));

        assertEquals(3, result.size());

        assertEquals(LocalDate.parse("2024-01-01"), result.get(0).getDate());
        assertEquals("Groceries", result.get(0).getCategory());
        assertEquals(new BigDecimal("123.45"), result.get(0).getAmount());

        assertEquals(LocalDate.parse("2024-01-03"), result.get(2).getDate());
        assertEquals(new BigDecimal("19.99"), result.get(2).getAmount());
    }

    @Test
    void testIgnoresEmptyLines() throws IOException {
        String csv = """
            2024-02-01,Gas,30.00

            2024-02-02,Other,10.00

            """;

        List<Transaction> result = TransactionReader.readFromCsv(new StringReader(csv));

        assertEquals(2, result.size());
    }

    @Test
    void testMalformedCsvThrowsException() {
        String badCsv = "2024-01-01,Groceries"; // Missing amount column

        IOException ex = assertThrows(IOException.class,
                () -> TransactionReader.readFromCsv(new StringReader(badCsv)));

        assertTrue(ex.getMessage().contains("Malformed CSV at line 1"));
    }

    @Test
    void testInvalidDateThrowsException() {
        String badDateCsv = "not-a-date,Groceries,10.00";

        IOException ex = assertThrows(IOException.class,
                () -> TransactionReader.readFromCsv(new StringReader(badDateCsv)));

        assertTrue(ex.getMessage().contains("Invalid data at line 1"));
    }

    @Test
    void testInvalidAmountThrowsException() {
        String badAmountCsv = "2024-01-01,Groceries,abc";

        IOException ex = assertThrows(IOException.class,
                () -> TransactionReader.readFromCsv(new StringReader(badAmountCsv)));

        assertTrue(ex.getMessage().contains("Invalid data at line 1"));
    }
}
