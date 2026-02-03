package view;

import javax.swing.*;
import java.io.File;
import model.CommunicationHub;

public class Application {
    private static final String DATA_FILE = "communication_hub_data.txt";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Create CommunicationHub instance
                CommunicationHub CommunicationHub = new CommunicationHub();

                // Try to load data
                File f = new File(DATA_FILE);
                if (f.exists()) {
                    try {
                        model.utilities.PersistenceManager.load(CommunicationHub, DATA_FILE);
                    } catch (Exception e) {
                        e.printStackTrace();
                        initializeData(CommunicationHub);
                    }
                } else {
                    // Initialize with some test data if no file exists
                    initializeData(CommunicationHub);
                }

                // Add shutdown hook to save data
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    try {
                        model.utilities.PersistenceManager.save(CommunicationHub, DATA_FILE);
                        System.out.println("Data saved successfully.");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }));

                // Show login frame
                new LoginFrame(CommunicationHub);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Error starting application: " + e.getMessage(),
                        "Startup Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }

    private static void initializeData(CommunicationHub CommunicationHub) {
        // Register some test users for demonstration
        CommunicationHub.register("John", "Doe", "555-123-4567");
        CommunicationHub.register("Jane", "Smith", "555-234-5678");
        CommunicationHub.register("Bob", "Jones", "555-345-6789");
        CommunicationHub.register("Alice", "Williams", "555-456-7890");
        CommunicationHub.register("Charlie", "Brown", "555-567-8901");
    }
}
