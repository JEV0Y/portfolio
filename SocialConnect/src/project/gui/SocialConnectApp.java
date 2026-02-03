package project.gui;

import project.*;
import project.enums.PageAlgorithm;

/**
 * Main class to launch the SocialConnect application GUI.
 */
public class SocialConnectApp {
    public static void main(String[] args) {
        // Create SocialConnect instance
        SocialConnect SocialConnect = new SocialConnect("SocialConnect Platform", PageAlgorithm.Newest);
        
        // Launch GUI
        javax.swing.SwingUtilities.invokeLater(() -> {
            MainWindow mainWindow = new MainWindow(SocialConnect);
            mainWindow.setVisible(true);
        });
    }
}
