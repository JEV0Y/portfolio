package view;

import javax.swing.*;
import model.PhoneNumber;
import model.CommunicationHub;
import java.awt.*;

public class RegistrationFrame extends JFrame {
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField phoneNumberField;
    private JLabel errorLabel;
    private JLabel successLabel;
    private CommunicationHub CommunicationHub;
    private LoginFrame loginFrame;

    public RegistrationFrame(CommunicationHub CommunicationHub, LoginFrame loginFrame) {
        this.CommunicationHub = CommunicationHub;
        this.loginFrame = loginFrame;

        setTitle("WhatsApp Registration");
        setSize(450, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel titleLabel = new JLabel("Create New Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBounds(20, 20, 400, 30);
        panel.add(titleLabel);

        JLabel firstNameLabel = new JLabel("First Name:");
        firstNameLabel.setBounds(20, 70, 150, 25);
        panel.add(firstNameLabel);

        firstNameField = new JTextField();
        firstNameField.setBounds(20, 95, 390, 35);
        firstNameField.setFont(firstNameField.getFont().deriveFont(14f));
        panel.add(firstNameField);

        JLabel lastNameLabel = new JLabel("Last Name:");
        lastNameLabel.setBounds(20, 140, 150, 25);
        panel.add(lastNameLabel);

        lastNameField = new JTextField();
        lastNameField.setBounds(20, 165, 390, 35);
        lastNameField.setFont(lastNameField.getFont().deriveFont(14f));
        panel.add(lastNameField);

        JLabel phoneLabel = new JLabel("Phone Number (XXX-XXX-XXXX):");
        phoneLabel.setBounds(20, 210, 300, 25);
        panel.add(phoneLabel);

        phoneNumberField = new JTextField();
        phoneNumberField.setBounds(20, 235, 390, 35);
        phoneNumberField.setFont(phoneNumberField.getFont().deriveFont(14f));
        panel.add(phoneNumberField);

        errorLabel = new JLabel("");
        errorLabel.setBounds(20, 280, 390, 25);
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(errorLabel);

        successLabel = new JLabel("");
        successLabel.setBounds(20, 305, 390, 25);
        successLabel.setForeground(new Color(0, 128, 0));
        successLabel.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(successLabel);

        JButton registerButton = new JButton("Register");
        registerButton.setBounds(20, 340, 180, 40);
        registerButton.setFont(registerButton.getFont().deriveFont(14f));
        registerButton.addActionListener(e -> handleRegistration());
        panel.add(registerButton);

        JButton cancelButton = new JButton("Back to Login");
        cancelButton.setBounds(230, 340, 180, 40);
        cancelButton.setFont(cancelButton.getFont().deriveFont(14f));
        cancelButton.addActionListener(e -> goBackToLogin());
        panel.add(cancelButton);

        add(panel);
        setVisible(true);
    }

    private void handleRegistration() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String phoneNumber = phoneNumberField.getText().trim();

        errorLabel.setText("");
        successLabel.setText("");

        // Validation
        if (firstName.isEmpty() || lastName.isEmpty() || phoneNumber.isEmpty()) {
            errorLabel.setText("Error: All fields are required");
            return;
        }

        if (!PhoneNumber.isValid(phoneNumber)) {
            errorLabel.setText("Error: Invalid phone format. Use XXX-XXX-XXXX");
            return;
        }

        // Check if phone already exists
        String[] parts = phoneNumber.split("-");
        if (CommunicationHub.findUser(new PhoneNumber(parts[0], parts[1], parts[2])) != null) {
            errorLabel.setText("Error: This phone number is already registered");
            return;
        }

        // Attempt registration
        try {
            boolean success = CommunicationHub.register(firstName, lastName, phoneNumber);
            if (success) {
                successLabel.setText("Ã¢Å“â€œ Registration successful! Redirecting to login...");
                firstNameField.setText("");
                lastNameField.setText("");
                phoneNumberField.setText("");

                // Redirect to login after a short delay
                Timer timer = new Timer(1500, e -> {
                    dispose();
                    new LoginFrame(CommunicationHub);
                });
                timer.setRepeats(false);
                timer.start();
            } else {
                errorLabel.setText("Error: Registration failed. Please try again");
            }
        } catch (Exception ex) {
            errorLabel.setText("Error: " + ex.getMessage());
        }
    }

    private void goBackToLogin() {
        dispose();
        new LoginFrame(CommunicationHub);
    }
}


