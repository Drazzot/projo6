package com.creditcardcalculatorbrionblais.app;

import javax.swing.SwingUtilities;
import com.creditcardcalculatorbrionblais.view.CreditCardCalculatorApp;

/**
 * Simple entry point for the CreditCardCalculatorBrionBlais application.
 * Launches the Swing UI defined in CreditCardCalculatorApp.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CreditCardCalculatorApp app = new CreditCardCalculatorApp();
            app.setVisible(true);
        });
    }
}