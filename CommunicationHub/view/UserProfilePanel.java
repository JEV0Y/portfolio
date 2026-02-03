package view;

import javax.swing.*;
import model.User;
import model.CommunicationHub;
import java.awt.*;

public class UserProfilePanel extends JPanel {
    private CommunicationHub CommunicationHub;
    private User loggedInUser;
    private JTextArea profileArea;
    private JTextArea resultArea;

    public UserProfilePanel(CommunicationHub CommunicationHub, User loggedInUser) {
        this.CommunicationHub = CommunicationHub;
        this.loggedInUser = loggedInUser;
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBorder(BorderFactory.createTitledBorder("Profile Operations"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Update First Name
        JPanel firstNamePanel = createPanelWithBorder("Update First Name");
        JTextField firstNameField = new JTextField();
        JButton updateFirstNameButton = new JButton("Update");
        updateFirstNameButton.addActionListener(e -> {
            try {
                String newName = firstNameField.getText().trim();
                if (newName.isEmpty()) {
                    showResult("Error: First name cannot be empty");
                    return;
                }
                boolean success = loggedInUser.setFirstname(newName);
                showResult(success ? "First name updated to: " + newName : "Failed to update first name");
                firstNameField.setText("");
                updateProfile();
            } catch (Exception ex) {
                showResult("Error: " + ex.getMessage());
            }
        });

        firstNamePanel.add(new JLabel("New First Name:"), createGbc(0, 0, 0.3));
        firstNamePanel.add(firstNameField, createGbc(1, 0, 0.7));
        firstNamePanel.add(updateFirstNameButton, createGbc(1, 1, 0));

        controlPanel.add(firstNamePanel, gbc);

        // Update Last Name
        gbc.gridy++;
        JPanel lastNamePanel = createPanelWithBorder("Update Last Name");
        JTextField lastNameField = new JTextField();
        JButton updateLastNameButton = new JButton("Update");
        updateLastNameButton.addActionListener(e -> {
            try {
                String newName = lastNameField.getText().trim();
                if (newName.isEmpty()) {
                    showResult("Error: Last name cannot be empty");
                    return;
                }
                boolean success = loggedInUser.setLastname(newName);
                showResult(success ? "Last name updated to: " + newName : "Failed to update last name");
                lastNameField.setText("");
                updateProfile();
            } catch (Exception ex) {
                showResult("Error: " + ex.getMessage());
            }
        });

        lastNamePanel.add(new JLabel("New Last Name:"), createGbc(0, 0, 0.3));
        lastNamePanel.add(lastNameField, createGbc(1, 0, 0.7));
        lastNamePanel.add(updateLastNameButton, createGbc(1, 1, 0));

        controlPanel.add(lastNamePanel, gbc);

        // Buttons Panel
        gbc.gridy++;
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 5, 5));

        // Get Phone Number
        JButton phoneButton = new JButton("View Phone Number");
        phoneButton.addActionListener(e -> {
            try {
                showResult("Your phone number: " + loggedInUser.getPhoneNumber().toString());
            } catch (Exception ex) {
                showResult("Error: " + ex.getMessage());
            }
        });
        buttonPanel.add(phoneButton);

        // View Full Information
        JButton fullInfoButton = new JButton("View Full Profile Info");
        fullInfoButton.addActionListener(e -> {
            try {
                showResult(loggedInUser.getFullInformation());
            } catch (Exception ex) {
                showResult("Error: " + ex.getMessage());
            }
        });
        buttonPanel.add(fullInfoButton);

        controlPanel.add(buttonPanel, gbc);

        // Push everything up
        gbc.gridy++;
        gbc.weighty = 1.0;
        controlPanel.add(new JLabel(), gbc);

        JScrollPane scrollPane = new JScrollPane(controlPanel);
        add(scrollPane, BorderLayout.WEST);

        // Profile Display Area
        profileArea = new JTextArea();
        profileArea.setEditable(false);
        profileArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        profileArea.setBorder(BorderFactory.createTitledBorder("Current Profile"));
        JScrollPane profileScroll = new JScrollPane(profileArea);
        add(profileScroll, BorderLayout.CENTER);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resultArea.setBorder(BorderFactory.createTitledBorder("Operation Results"));
        JScrollPane resultScroll = new JScrollPane(resultArea);
        resultScroll.setPreferredSize(new Dimension(300, 150));
        add(resultScroll, BorderLayout.SOUTH);

        updateProfile();
    }

    private void updateProfile() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("Name: ").append(loggedInUser.getFullName()).append("\n");
            sb.append("First Name: ").append(loggedInUser.getFirstname()).append("\n");
            sb.append("Last Name: ").append(loggedInUser.getLastname()).append("\n");
            sb.append("Phone: ").append(loggedInUser.getPhoneNumber().toString()).append("\n");
            profileArea.setText(sb.toString());
        } catch (Exception ex) {
            profileArea.setText("Error loading profile: " + ex.getMessage());
        }
    }

    private JPanel createPanelWithBorder(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        return panel;
    }

    private GridBagConstraints createGbc(int x, int y, double wx) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = x;
        g.gridy = y;
        g.weightx = wx;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(2, 2, 2, 2);
        return g;
    }

    private void showResult(String message) {
        resultArea.setText(message);
        resultArea.setCaretPosition(0);
    }
}


