package view;

import javax.swing.*;
import model.PhoneNumber;
import model.User;
import model.CommunicationHub;

public class LoginFrame extends JFrame {
    private JTextField phoneNumberField;
    private JButton loginButton;
    private JLabel errorLabel;
    private CommunicationHub CommunicationHub;
    private User loggedInUser;

    public LoginFrame(CommunicationHub CommunicationHub) {
        this.CommunicationHub = CommunicationHub;
        setTitle("CommunicationHub Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel instructionLabel = new JLabel("Enter your phone number to login:");
        instructionLabel.setBounds(20, 20, 350, 30);
        panel.add(instructionLabel);

        JLabel phoneLabel = new JLabel("Phone Number (XXX-XXX-XXXX):");
        phoneLabel.setBounds(20, 60, 200, 30);
        panel.add(phoneLabel);

        phoneNumberField = new JTextField();
        phoneNumberField.setBounds(20, 90, 340, 35);
        phoneNumberField.setFont(phoneNumberField.getFont().deriveFont(14f));
        panel.add(phoneNumberField);

        loginButton = new JButton("Login");
        loginButton.setBounds(20, 140, 160, 40);
        loginButton.setFont(loginButton.getFont().deriveFont(14f));
        loginButton.addActionListener(e -> handleLogin());
        panel.add(loginButton);

        JButton registerButton = new JButton("Register");
        registerButton.setBounds(200, 140, 160, 40);
        registerButton.setFont(registerButton.getFont().deriveFont(14f));
        registerButton.addActionListener(e -> openRegistration());
        panel.add(registerButton);

        errorLabel = new JLabel("");
        errorLabel.setBounds(20, 190, 340, 30);
        errorLabel.setForeground(java.awt.Color.RED);
        panel.add(errorLabel);

        add(panel);
        setVisible(true);
    }

    private void handleLogin() {
        String phoneNumber = phoneNumberField.getText().trim();
        errorLabel.setText("");

        if (phoneNumber.isEmpty()) {
            errorLabel.setText("Please enter a phone number");
            return;
        }

        if (!PhoneNumber.isValid(phoneNumber)) {
            errorLabel.setText("Invalid phone number format (use XXX-XXX-XXXX)");
            return;
        }

        loggedInUser = CommunicationHub.findUser(new PhoneNumber(
                phoneNumber.split("-")[0],
                phoneNumber.split("-")[1],
                phoneNumber.split("-")[2]
        ));

        if (loggedInUser == null) {
            errorLabel.setText("user not found. Please register first.");
            return;
        }

        dispose();
        new MainGUI(CommunicationHub, loggedInUser);
    }

    private void openRegistration() {
        dispose();
        new RegistrationFrame(CommunicationHub, this);
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }
}


