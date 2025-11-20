package com.creditcardcalculatorbrionblais.view;

import com.creditcardcalculatorbrionblais.controller.MainController;
import com.creditcardcalculatorbrionblais.model.strategy.AccountSimulator;
import com.creditcardcalculatorbrionblais.model.transaction.RewardPolicy;
import com.creditcardcalculatorbrionblais.model.strategy.*;
import com.creditcardcalculatorbrionblais.model.transaction.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.FileReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;



/**
 * Enhanced Swing GUI implementing:
 * - UseCaseDescription full requirements
 * - Settings page for APR, fees, grace period, due date, starting balance,
 *   reward policy, interest method
 * - Dropdown for payment policy (Early, WallStreet, Light, Heavy)
 * - Visualization selection (daily/weekly/monthly/yearly)
 */
public class CreditCardCalculatorApp extends JFrame {

    private final MainController controller = new MainController();
    private List<Transaction> transactions;

    // UI components
    private JTextArea output = new JTextArea(12, 70);
    private JLabel status = new JLabel("No file selected");

    // SETTINGS inputs
    private JComboBox<String> paymentPolicyBox;
    private JComboBox<String> interestMethodBox;
    private JComboBox<String> visualizationBox;

    private JTextField aprField;
    private JTextField penaltyAprField;
    private JTextField lateFeeField;
    private JTextField returnedFeeField;
    private JTextField dueDateField;
    private JTextField gracePeriodField;
    private JTextField startingBalanceField;

    private JTextField rewardGroceriesField;
    private JTextField rewardGasField;
    private JTextField rewardOtherField;
    
    private JTextField lightStartIndexField;
    private JTextField heavyStartIndexField;
    private JTextField heavyLateCycleField;
    
    private JCheckBox digDeeperCheckBox;
    
    private JTextField startDateField;
    private JTextField endDateField;
    private String startDateString;
    private String endDateString;

    

    public CreditCardCalculatorApp() {
        super("Credit Card Cost Calculator — Brion Blais");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(mainToolbar(), BorderLayout.NORTH);
        add(centerPanel(), BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    // -------------------------------------------------------------------------
    // TOP BAR
    // -------------------------------------------------------------------------
    private JPanel mainToolbar() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton openBtn = new JButton("Open CSV");
        openBtn.addActionListener(e -> onOpenCsv());

        JButton settingsBtn = new JButton("Settings");
        settingsBtn.addActionListener(e -> openSettingsDialog());

        JButton runBtn = new JButton("Run Simulation");
        runBtn.addActionListener(e -> onRunSimulation());

        top.add(openBtn);
        top.add(settingsBtn);
        top.add(runBtn);

        return top;
    }

    // -------------------------------------------------------------------------
    // CENTER PANEL
    // -------------------------------------------------------------------------
    private JScrollPane centerPanel() {
        output.setEditable(false);
        return new JScrollPane(output);
    }

    // -------------------------------------------------------------------------
    // SETTINGS WINDOW
    // -------------------------------------------------------------------------
    private void openSettingsDialog() {
        JDialog dlg = new JDialog(this, "Simulation Settings", true);
        dlg.setLayout(new BorderLayout());

        JPanel p = new JPanel();
        p.setLayout(new GridLayout(0, 2, 8, 6));

        // Payment Policy
        p.add(new JLabel("Payment Policy:"));
        paymentPolicyBox = new JComboBox<>(
                new String[]{
                        "Early Transactor",
                        "Wall Street Transactor",
                        "Light Revolver",
                        "Heavy Revolver"
                });
        p.add(paymentPolicyBox);

        // Interest method
        p.add(new JLabel("Interest Method:"));
        interestMethodBox = new JComboBox<>(
                new String[]{"Synchrony Daily Balance", "Average Daily Balance"});
        p.add(interestMethodBox);

        p.add(new JLabel("Start Date (YYYY-MM-DD):"));
        startDateField = new JTextField();  // leave blank until CSV load
        p.add(startDateField);

        p.add(new JLabel("End Date (YYYY-MM-DD):"));
        endDateField = new JTextField();    // leave blank until CSV load
        p.add(endDateField);


        p.add(new JLabel("Dig Deeper (Show Charts):"));
        digDeeperCheckBox = new JCheckBox();
        digDeeperCheckBox.setSelected(false); // default unchecked
        p.add(digDeeperCheckBox);
        
        // Visualization
        p.add(new JLabel("Visualization Level:"));
        visualizationBox = new JComboBox<>(
                new String[]{"Daily", "Weekly", "Monthly", "Yearly"});
        p.add(visualizationBox);

        // APR settings
        aprField = new JTextField("0.35");
        penaltyAprField = new JTextField("0.3999");
        lateFeeField = new JTextField("35");
        returnedFeeField = new JTextField("25");
        dueDateField = new JTextField("20");
        gracePeriodField = new JTextField("25");
        startingBalanceField = new JTextField("0");

        p.add(new JLabel("APR (e.g., 0.35):")); p.add(aprField);
        p.add(new JLabel("Penalty APR:")); p.add(penaltyAprField);
        p.add(new JLabel("Late Fee ($):")); p.add(lateFeeField);
        p.add(new JLabel("Returned Payment Fee ($):")); p.add(returnedFeeField);
        p.add(new JLabel("Due Date (1–31):")); p.add(dueDateField);
        p.add(new JLabel("Grace Period (days):")); p.add(gracePeriodField);
        p.add(new JLabel("Starting Balance ($):")); p.add(startingBalanceField);

        // Reward policy
        rewardGroceriesField = new JTextField("0.03");
        rewardGasField = new JTextField("0.02");
        rewardOtherField = new JTextField("0.01");

        p.add(new JLabel("Groceries Reward %:")); p.add(rewardGroceriesField);
        p.add(new JLabel("Gas Reward %:")); p.add(rewardGasField);
        p.add(new JLabel("Other Reward %:")); p.add(rewardOtherField);

        dlg.add(new JScrollPane(p), BorderLayout.CENTER);
        
        // --- Light Revolver config ---
        p.add(new JLabel("Light Revolver Start Index:"));
        lightStartIndexField = new JTextField("0");
        p.add(lightStartIndexField);

        // --- Heavy Revolver config ---
        p.add(new JLabel("Heavy Revolver Start Index:"));
        heavyStartIndexField = new JTextField("0");
        p.add(heavyStartIndexField);

        p.add(new JLabel("Heavy Revolver Late Every N Cycles:"));
        heavyLateCycleField = new JTextField("6"); // default matches your use case
        p.add(heavyLateCycleField);


        // OK button
        JButton ok = new JButton("Save");
        ok.addActionListener(e -> dlg.dispose());
        dlg.add(ok, BorderLayout.SOUTH);

        dlg.pack();
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    // -------------------------------------------------------------------------
    // CSV LOAD
    // -------------------------------------------------------------------------
    private void onOpenCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("CSV files", "csv"));
        int res = chooser.showOpenDialog(this);
        if (res != JFileChooser.APPROVE_OPTION) return;

        try {
            String path = chooser.getSelectedFile().getAbsolutePath();
            try (FileReader fr = new FileReader(path)) {
                transactions = TransactionReader.readFromCsv(fr);
            }

            status.setText("Loaded: " + transactions.size() + " transactions");
            output.setText(summarizeLoaded(transactions));

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to read file: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private String summarizeLoaded(List<Transaction> txs) {
        BigDecimal total = txs.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LocalDate first = txs.stream()
                .map(Transaction::getDate)
                .min(LocalDate::compareTo).orElse(null);

        LocalDate last = txs.stream()
                .map(Transaction::getDate)
                .max(LocalDate::compareTo).orElse(null);

        startDateString = first.toString();
        endDateString = last.toString();
        
      
        return "Transactions Loaded\n" +
                "- Count: " + txs.size() + "\n" +
                "- Total Amount: $" + total + "\n" +
                "- Date Range: " + first + " to " + last + "\n\n" +
                "Open Settings or Run Simulation.";
    }

    // -------------------------------------------------------------------------
    // SIMULATION
    // -------------------------------------------------------------------------
    private void onRunSimulation() {

        if (transactions == null || transactions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No transactions loaded",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //Parse dates
        LocalDate startDate;
        LocalDate endDate;

        try {
            startDate = LocalDate.parse(startDateString.trim());
            endDate = LocalDate.parse(endDateString.trim());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid start or end date. Use format YYYY-MM-DD.",
                    "Date Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Ensure correct order
        if (endDate.isBefore(startDate)) {
            JOptionPane.showMessageDialog(this,
                    "End date must be AFTER start date.",
                    "Date Range Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Build reward policy
            RewardPolicy rewards = new RewardPolicy(
                    new BigDecimal(rewardGroceriesField.getText()),
                    new BigDecimal(rewardGasField.getText()),
                    new BigDecimal(rewardOtherField.getText())
            );

            // Choose payment policy
            PaymentStrategy ps = buildPaymentStrategy();

            // Interest method
            AccountSimulator.InterestMethod method =
                    interestMethodBox.getSelectedIndex() == 0
                            ? AccountSimulator.InterestMethod.SYNCHRONY_DAILY_BALANCE
                            : AccountSimulator.InterestMethod.AVERAGE_DAILY_BALANCE;

            // Input parameters
            BigDecimal startingBalance = new BigDecimal(startingBalanceField.getText());
            int due = Integer.parseInt(dueDateField.getText());
            int grace = Integer.parseInt(gracePeriodField.getText());
            
            LocalDate start = startDate;
            LocalDate end = endDate;


            AccountSimulator.Summary sum = controller.simulate(
                    transactions,
                    rewards,
                    ps,
                    method,
                    startingBalance,
                    start,
                    end
            );

            // Output results
            StringBuilder sb = new StringBuilder();
            sb.append("=== Simulation Summary ===\n");
            sb.append("Payment Policy: ").append(paymentPolicyBox.getSelectedItem()).append("\n");
            sb.append("Interest Method: ").append(interestMethodBox.getSelectedItem()).append("\n\n");
            sb.append("Beginning Balance: $").append(sum.beginningBalance).append("\n");
            sb.append("Ending Balance: $").append(sum.endingBalance).append("\n");
            sb.append("Total Payments: $").append(sum.totalPayments).append("\n");
            sb.append("Total Interest: $").append(sum.totalInterest).append("\n");
            sb.append("Total Fees: $").append(sum.totalFees).append("\n");
            sb.append("Total Rewards: $").append(sum.totalRewards).append("\n");

            output.setText(sb.toString());
            status.setText("Simulation complete");
            
            if (digDeeperCheckBox != null && digDeeperCheckBox.isSelected()) {
                showVisualizationWindow(sum);
            }


        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Simulation failed: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private PaymentStrategy buildPaymentStrategy() {
        String choice = (String) paymentPolicyBox.getSelectedItem();

        return switch (choice) {
            case "Early Transactor" -> new EarlyTransactor();
            case "Wall Street Transactor" -> new WallStreetTransactor();
            case "Light Revolver" -> new LightRevolver(Integer.parseInt(lightStartIndexField.getText()));
            case "Heavy Revolver" -> new HeavyRevolver(Integer.parseInt(heavyStartIndexField.getText()),Integer.parseInt(heavyLateCycleField.getText()));
            default -> new WallStreetTransactor();
        };
    }

    private void showVisualizationWindow(AccountSimulator.Summary sum) {
        String level = (String) visualizationBox.getSelectedItem();

        // For now we chart balances, but this can be expanded to
        // interest, fees, rewards, payments, etc.
        java.util.List<LocalDate> dates = sum.dates;
        java.util.List<BigDecimal> values = sum.balances;

        String title = switch (level) {
            case "Daily" -> "Daily Balance Over Time";
            case "Weekly" -> "Weekly Balance Overview";
            case "Monthly" -> "Monthly Balance Overview";
            case "Yearly" -> "Yearly Balance Overview";
            default -> "Balance Over Time";
        };

        JFrame frame = new JFrame("Dig Deeper Graph");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Use the custom JPanel to draw the chart
        SimpleLineChartPanel chartPanel =
                new SimpleLineChartPanel(title, dates, values);

        frame.add(chartPanel);
        frame.pack();
        frame.setLocationRelativeTo(this);
        frame.setVisible(true);
    }

    
}
