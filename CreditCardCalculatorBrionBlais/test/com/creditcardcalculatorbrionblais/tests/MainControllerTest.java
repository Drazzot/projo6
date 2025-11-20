package com.creditcardcalculatorbrionblais.tests;

import com.creditcardcalculatorbrionblais.controller.MainController;
import com.creditcardcalculatorbrionblais.model.strategy.*;
import com.creditcardcalculatorbrionblais.model.transaction.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for MainController.
 * Validates CSV loading and simulation orchestration.
 */
public class MainControllerTest {
    
    private MainController controller;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    public void setUp() {
        controller = new MainController();
    }
    
    @Test
    public void testLoadValidCsv() throws IOException {
        Path csvFile = tempDir.resolve("test.csv");
        try (FileWriter writer = new FileWriter(csvFile.toFile())) {
            writer.write("2024-01-15,Groceries,100.00\n");
            writer.write("2024-01-16,Gas,50.00\n");
        }
        
        List<Transaction> transactions = controller.loadCsv(csvFile.toString());
        
        assertEquals(2, transactions.size());
        assertEquals("Groceries", transactions.get(0).getCategory());
        assertEquals(new BigDecimal("50.00"), transactions.get(1).getAmount());
    }
    
    @Test
    public void testLoadEmptyCsv() throws IOException {
        Path csvFile = tempDir.resolve("empty.csv");
        try (FileWriter writer = new FileWriter(csvFile.toFile())) {
            // Empty file
        }
        
        List<Transaction> transactions = controller.loadCsv(csvFile.toString());
        
        assertTrue(transactions.isEmpty());
    }
    
    @Test
    public void testLoadNonExistentFileThrowsException() {
        assertThrows(IOException.class, () -> {
            controller.loadCsv("nonexistent.csv");
        });
    }
    
    @Test
    public void testSimulateWithEarlyTransactor() throws IOException {
        Path csvFile = tempDir.resolve("test.csv");
        try (FileWriter writer = new FileWriter(csvFile.toFile())) {
            writer.write("2024-01-15,Groceries,100.00\n");
        }
        
        List<Transaction> txs = controller.loadCsv(csvFile.toString());
        RewardPolicy rewards = RewardPolicy.defaultPolicy();
        PaymentStrategy payment = new EarlyTransactor();
        
        AccountSimulator.Summary summary = controller.simulate(
            txs,
            rewards,
            payment,
            AccountSimulator.InterestMethod.SYNCHRONY_DAILY_BALANCE,
            BigDecimal.ZERO,
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 1, 31)
        );
        
        assertNotNull(summary);
        assertNotNull(summary.endingBalance);
        assertNotNull(summary.totalPayments);
    }
    
    @Test
    public void testSimulateWithWallStreetTransactor() throws IOException {
        Path csvFile = tempDir.resolve("test.csv");
        try (FileWriter writer = new FileWriter(csvFile.toFile())) {
            writer.write("2024-01-10,Gas,75.00\n");
        }
        
        List<Transaction> txs = controller.loadCsv(csvFile.toString());
        RewardPolicy rewards = RewardPolicy.defaultPolicy();
        PaymentStrategy payment = new WallStreetTransactor();
        
        AccountSimulator.Summary summary = controller.simulate(
            txs,
            rewards,
            payment,
            AccountSimulator.InterestMethod.AVERAGE_DAILY_BALANCE,
            BigDecimal.ZERO,
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 1, 31)
        );
        
        assertNotNull(summary);
        assertTrue(summary.totalRewards.compareTo(BigDecimal.ZERO) > 0);
    }
    
    @Test
    public void testSimulateWithStartingBalance() throws IOException {
        Path csvFile = tempDir.resolve("test.csv");
        try (FileWriter writer = new FileWriter(csvFile.toFile())) {
            writer.write("2024-01-15,Other,50.00\n");
        }
        
        List<Transaction> txs = controller.loadCsv(csvFile.toString());
        BigDecimal startBalance = new BigDecimal("500.00");
        
        AccountSimulator.Summary summary = controller.simulate(
            txs,
            RewardPolicy.defaultPolicy(),
            new EarlyTransactor(),
            AccountSimulator.InterestMethod.SYNCHRONY_DAILY_BALANCE,
            startBalance,
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 1, 31)
        );
        
        assertEquals(startBalance, summary.beginningBalance);
    }
    
    @Test
    public void testSimulateMultipleMonths() throws IOException {
        Path csvFile = tempDir.resolve("test.csv");
        try (FileWriter writer = new FileWriter(csvFile.toFile())) {
            writer.write("2024-01-15,Groceries,100.00\n");
            writer.write("2024-02-15,Gas,75.00\n");
            writer.write("2024-03-15,Other,50.00\n");
        }
        
        List<Transaction> txs = controller.loadCsv(csvFile.toString());
        
        AccountSimulator.Summary summary = controller.simulate(
            txs,
            RewardPolicy.defaultPolicy(),
            new WallStreetTransactor(),
            AccountSimulator.InterestMethod.SYNCHRONY_DAILY_BALANCE,
            BigDecimal.ZERO,
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 3, 31)
        );
        
        assertNotNull(summary);
        assertTrue(summary.dates.size() >= 3);
    }
    
    @Test
    public void testSimulateEmptyTransactionList() {
        AccountSimulator.Summary summary = controller.simulate(
            List.of(),
            RewardPolicy.defaultPolicy(),
            new EarlyTransactor(),
            AccountSimulator.InterestMethod.SYNCHRONY_DAILY_BALANCE,
            BigDecimal.ZERO,
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 1, 31)
        );
        
        assertNotNull(summary);
        assertEquals(BigDecimal.ZERO, summary.beginningBalance);
    }
    
    @Test
    public void testSimulateWithLightRevolver() throws IOException {
        Path csvFile = tempDir.resolve("test.csv");
        try (FileWriter writer = new FileWriter(csvFile.toFile())) {
            writer.write("2024-01-15,Groceries,500.00\n");
        }
        
        List<Transaction> txs = controller.loadCsv(csvFile.toString());
        
        AccountSimulator.Summary summary = controller.simulate(
            txs,
            RewardPolicy.defaultPolicy(),
            new LightRevolver(0),
            AccountSimulator.InterestMethod.SYNCHRONY_DAILY_BALANCE,
            BigDecimal.ZERO,
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 6, 30)
        );
        
        assertNotNull(summary);
        assertTrue(summary.totalInterest.compareTo(BigDecimal.ZERO) > 0);
    }
}