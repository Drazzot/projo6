package com.creditcardcalculatorbrionblais.view;

import com.creditcardcalculatorbrionblais.controller.MainWindowController;
import com.creditcardcalculatorbrionblais.model.account.SimulationResult;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.nio.file.Path;

/**
 * Minimal Swing UI: file loader, strategy selection, simulate, and text output.
 */
public class MainWindow {

    private final MainWindowController controller;
    private final JFrame frame;
    private final JTextArea outputArea;
    private final JLabel statusLabel;
    private final JComboBox<String> strategyCombo;

    public MainWindow(MainWindowController controller) {
        this.controller = controller;
        frame = new JFrame("CreditCardCalculatorBrionBlais");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton loadBtn = new JButton("Load CSV");
        loadBtn.addActionListener(e -> onLoad());
        strategyCombo = new JComboBox<>(new String[]{"Early", "WallStreet"});
        JButton simBtn = new JButton("Simulate");
        simBtn.addActionListener(e -> onSimulate());

        top.add(loadBtn);
        top.add(new JLabel("Payment Strategy:"));
        top.add(strategyCombo);
        top.add(simBtn);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(outputArea);

        statusLabel = new JLabel("Ready");

        frame.getContentPane().add(top, BorderLayout.NORTH);
        frame.getContentPane().add(scroll, BorderLayout.CENTER);
        frame.getContentPane().add(statusLabel, BorderLayout.SOUTH);
    }

    public void show() { frame.setVisible(true); }

    private void onLoad() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("CSV files", "csv"));
        int res = chooser.showOpenDialog(frame);
        if (res == JFileChooser.APPROVE_OPTION) {
            Path p = chooser.getSelectedFile().toPath();
            controller.loadCsv(p);
        }
    }

    private void onSimulate() {
        outputArea.setText("");
        String strategy = (String) strategyCombo.getSelectedItem();
        controller.runSimulation(strategy);
    }

    public void setStatus(String s) { statusLabel.setText(s); }

    public void showError(String s) { JOptionPane.showMessageDialog(frame, s, "Error", JOptionPane.ERROR_MESSAGE); }

    public void displayResult(SimulationResult r) {
        StringBuilder sb = new StringBuilder();
        sb.append("Simulation Result\n");
        sb.append("=================\n");
        sb.append("Total Interest: ").append(r.totalInterest).append("\n");
        sb.append("Total Rewards: ").append(r.totalRewards).append("\n");
        sb.append("Total Fees: ").append(r.totalFees).append("\n");
        sb.append("Total Payments: ").append(r.totalPayments).append("\n");
        sb.append("Ending Balance: ").append(r.endingBalance).append("\n\n");
        sb.append("Log:\n");
        for (String line : r.log) { sb.append(line).append("\n"); }
        outputArea.setText(sb.toString());
        setStatus("Simulation complete");
    }
}
