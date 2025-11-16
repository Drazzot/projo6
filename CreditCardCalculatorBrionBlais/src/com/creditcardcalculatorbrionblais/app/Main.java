package com.creditcardcalculatorbrionblais.app;

import com.creditcardcalculatorbrionblais.controller.MainWindowController;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Ensure UI runs on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainWindowController controller = new MainWindowController();
            controller.start();
        });
    }
}
