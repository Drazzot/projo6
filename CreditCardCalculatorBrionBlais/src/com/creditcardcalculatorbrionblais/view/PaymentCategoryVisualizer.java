package com.creditcardcalculatorbrionblais.view;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import javax.swing.JPanel;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class PaymentCategoryVisualizer {
    public static JPanel createChartPanel() throws Exception {
        String dataDir = "data";
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Map<Category, Map<Month, Amount>>
        Map<String, Map<String, Double>> categoryMonthTotals = new HashMap<>();
        DateTimeFormatter inputFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter ymFmt = DateTimeFormatter.ofPattern("yyyy-MM");

        // Read all CSV files in data/
        Files.list(Paths.get(dataDir))
                .filter(p -> !Files.isDirectory(p) && p.toString().endsWith(".csv"))
                .forEach(path -> {
                    try (BufferedReader buff = new BufferedReader(new FileReader(path.toFile()))) {
                        String line;
                        while ((line = buff.readLine()) != null) {
                            line = line.trim();
                            if (line.isEmpty() || line.startsWith("#")) continue; // skip empty/comment
                            String[] tokens = line.split(",", -1);
                            if (tokens.length < 3) continue;
                            String date = tokens[0].trim();
                            String category = tokens[1].trim();
                            String amountStr = tokens[2].trim();
                            if(date.isEmpty() || category.isEmpty() || amountStr.isEmpty()) continue;
                            LocalDate d = LocalDate.parse(date, inputFmt);
                            String ym = d.format(ymFmt);
                            double amount;
                            try {
                                amount = Double.parseDouble(amountStr);
                            } catch (NumberFormatException nfe) {
                                continue;
                            }
                            categoryMonthTotals.computeIfAbsent(category, k -> new HashMap<>())
                                .merge(ym, amount, Double::sum);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });

        // Fill dataset: [category, month] => total
        Set<String> allMonths = new TreeSet<>();
        categoryMonthTotals.values().forEach(map -> allMonths.addAll(map.keySet()));
        for (String category : categoryMonthTotals.keySet()) {
            Map<String, Double> monthTotals = categoryMonthTotals.get(category);
            for (String month : allMonths) {
                double total = monthTotals.getOrDefault(month, 0.0);
                dataset.addValue(total, category, month);
            }
        }

        JFreeChart lineChart = ChartFactory.createLineChart(
                "Category Spending Over Time",
                "Month",
                "Total Amount (USD)",
                dataset
        );
        return new ChartPanel(lineChart);
    }
}