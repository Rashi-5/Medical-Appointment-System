package com.clinic;

import com.clinic.gui.ClinicGUI;

import javax.swing.*;

/**
 * Entry point — launches the Swing GUI on the Event Dispatch Thread.
 */
public class ClinicApplication {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new ClinicGUI().setVisible(true);
        });
    }
}
