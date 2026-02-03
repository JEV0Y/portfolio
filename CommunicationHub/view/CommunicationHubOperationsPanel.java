package view;

import javax.swing.*;
import model.PhoneNumber;
import model.User;
import model.CommunicationHub;
import java.awt.*;
import java.util.TreeSet;

public class CommunicationHubOperationsPanel extends JPanel {
    private CommunicationHub CommunicationHub;
    private User loggedInUser;
    private JTextArea resultArea;

    public CommunicationHubOperationsPanel(CommunicationHub CommunicationHub, User loggedInUser) {
        this.CommunicationHub = CommunicationHub;
        this.loggedInUser = loggedInUser;
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBorder(BorderFactory.createTitledBorder("CommunicationHub Operations"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.NORTH;

        // === COLUMN 1 ===
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Register User
        JPanel registerPanel = createPanelWithBorder("Register New User");
        JTextField regFirstName = new JTextField();
        JTextField regLastName = new JTextField();
        JTextField regPhoneNumber = new JTextField();
        JButton regButton = new JButton("Register");
        regButton.addActionListener(e -> {
            try {
                String firstName = regFirstName.getText().trim();
                String lastName = regLastName.getText().trim();
                String phoneNumber = regPhoneNumber.getText().trim();

                if (firstName.isEmpty() || lastName.isEmpty() || phoneNumber.isEmpty()) {
                    showResult("Error: All fields must be filled");
                    return;
                }

                if (!PhoneNumber.isValid(phoneNumber)) {
                    showResult("Error: Invalid phone number format (use XXX-XXX-XXXX)");
                    return;
                }

                boolean success = CommunicationHub.register(firstName, lastName, phoneNumber);
                showResult(success ? "Successfully registered: " + firstName + " " + lastName
                        : "Registration failed (duplicate phone number?)");
                regFirstName.setText("");
                regLastName.setText("");
                regPhoneNumber.setText("");
            } catch (Exception ex) {
                showResult("Error: " + ex.getMessage());
            }
        });

        registerPanel.add(new JLabel("First Name:"), createGbc(0, 0));
        registerPanel.add(regFirstName, createGbc(1, 0));
        registerPanel.add(new JLabel("Last Name:"), createGbc(0, 1));
        registerPanel.add(regLastName, createGbc(1, 1));
        registerPanel.add(new JLabel("Phone:"), createGbc(0, 2));
        registerPanel.add(regPhoneNumber, createGbc(1, 2));
        GridBagConstraints btnGbc = createGbc(1, 3);
        btnGbc.weightx = 0;
        btnGbc.fill = GridBagConstraints.NONE;
        btnGbc.anchor = GridBagConstraints.EAST;
        registerPanel.add(regButton, btnGbc);

        controlPanel.add(registerPanel, gbc);

        // Find User by Name
        gbc.gridy++;
        JPanel findNamePanel = createPanelWithBorder("Find User by Name");
        JTextField findNameField = new JTextField();
        JButton findNameButton = createSearchButton("Find", e -> {
            String name = findNameField.getText().trim();
            if (!name.isEmpty()) {
                TreeSet<User> found = CommunicationHub.findUser(name);
                showSearchResults(found, "name: " + name);
            }
        });

        findNamePanel.add(new JLabel("Name:"), createGbc(0, 0));
        findNamePanel.add(findNameField, createGbc(1, 0));
        findNamePanel.add(findNameButton, createBtnGbc(1, 1));
        controlPanel.add(findNamePanel, gbc);

        // Find User by Name Substring
        gbc.gridy++;
        JPanel findSubstringPanel = createPanelWithBorder("Find by Name Substring");
        JTextField substringField = new JTextField();
        JButton substringButton = createSearchButton("Search", e -> {
            String sub = substringField.getText().trim();
            if (!sub.isEmpty()) {
                TreeSet<User> found = CommunicationHub.findUsersWithNameSubString(sub);
                showSearchResults(found, "substring: " + sub);
            }
        });

        findSubstringPanel.add(new JLabel("Substring:"), createGbc(0, 0));
        findSubstringPanel.add(substringField, createGbc(1, 0));
        findSubstringPanel.add(substringButton, createBtnGbc(1, 1));
        controlPanel.add(findSubstringPanel, gbc);

        // === COLUMN 2 ===
        gbc.gridx = 1;
        gbc.gridy = 0;

        // Find User by Phone Number
        JPanel findPhonePanel = createPanelWithBorder("Find User by Phone");
        JTextField phoneField = new JTextField();
        JButton findPhoneButton = new JButton("Find");
        findPhoneButton.addActionListener(e -> {
            try {
                String ph = phoneField.getText().trim();
                if (!ph.isEmpty() && PhoneNumber.isValid(ph)) {
                    String[] parts = ph.split("-");
                    User found = CommunicationHub.findUser(new PhoneNumber(parts[0], parts[1], parts[2]));
                    showResult(found != null ? "Found: " + found.toString() : "No User found");
                } else {
                    showResult("Invalid phone number");
                }
            } catch (Exception ex) {
                showResult("Error: " + ex.getMessage());
            }
        });

        findPhonePanel.add(new JLabel("Phone:"), createGbc(0, 0));
        findPhonePanel.add(phoneField, createGbc(1, 0));
        findPhonePanel.add(findPhoneButton, createBtnGbc(1, 1));
        controlPanel.add(findPhonePanel, gbc);

        // Find User by Phone Prefix
        gbc.gridy++;
        JPanel prefixPanel = createPanelWithBorder("Find by Phone Prefix");
        JTextField prefixField = new JTextField();
        JButton prefixButton = createSearchButton("Search", e -> {
            String pre = prefixField.getText().trim();
            if (!pre.isEmpty()) {
                TreeSet<User> found = CommunicationHub.findUserWithPhonePrefix(pre);
                showSearchResults(found, "prefix: " + pre);
            }
        });

        prefixPanel.add(new JLabel("Prefix:"), createGbc(0, 0));
        prefixPanel.add(prefixField, createGbc(1, 0));
        prefixPanel.add(prefixButton, createBtnGbc(1, 1));
        controlPanel.add(prefixPanel, gbc);

        // Find User by Phone Postfix
        gbc.gridy++;
        JPanel postfixPanel = createPanelWithBorder("Find by Phone Postfix");
        JTextField postfixField = new JTextField();
        JButton postfixButton = createSearchButton("Search", e -> {
            String post = postfixField.getText().trim();
            if (!post.isEmpty()) {
                TreeSet<User> found = CommunicationHub.findUserWithPhonePostfix(post);
                showSearchResults(found, "postfix: " + post);
            }
        });

        postfixPanel.add(new JLabel("Postfix:"), createGbc(0, 0));
        postfixPanel.add(postfixField, createGbc(1, 0));
        postfixPanel.add(postfixButton, createBtnGbc(1, 1));
        controlPanel.add(postfixPanel, gbc);

        // Find User by Phone Substring
        gbc.gridy++;
        JPanel phoneSubstringPanel = createPanelWithBorder("Find by Phone Substring");
        JTextField phoneSubField = new JTextField();
        JButton phoneSubButton = createSearchButton("Search", e -> {
            String sub = phoneSubField.getText().trim();
            if (!sub.isEmpty()) {
                TreeSet<User> found = CommunicationHub.findUserWithPhoneSubString(sub);
                showSearchResults(found, "substring: " + sub);
            }
        });

        phoneSubstringPanel.add(new JLabel("Substring:"), createGbc(0, 0));
        phoneSubstringPanel.add(phoneSubField, createGbc(1, 0));
        phoneSubstringPanel.add(phoneSubButton, createBtnGbc(1, 1));
        controlPanel.add(phoneSubstringPanel, gbc);

        // Get Full Information button (Spanning both cols at bottom)
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton infoButton = new JButton("Get All Users Info");
        infoButton.addActionListener(e -> showResult(CommunicationHub.getFullInformation()));
        controlPanel.add(infoButton, gbc);

        // Push everything up
        gbc.gridy++;
        gbc.weighty = 1.0;
        controlPanel.add(new JLabel(), gbc);

        JScrollPane scrollPane = new JScrollPane(controlPanel);
        add(scrollPane, BorderLayout.CENTER);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane resultScroll = new JScrollPane(resultArea);
        resultScroll.setPreferredSize(new Dimension(900, 200));
        add(resultScroll, BorderLayout.SOUTH);
    }

    private JPanel createPanelWithBorder(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        return panel;
    }

    private GridBagConstraints createGbc(int x, int y) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = x;
        g.gridy = y;
        g.weightx = (x == 1) ? 1.0 : 0.0;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(2, 2, 2, 2);
        return g;
    }

    private GridBagConstraints createBtnGbc(int x, int y) {
        GridBagConstraints g = createGbc(x, y);
        g.weightx = 0;
        g.fill = GridBagConstraints.NONE;
        g.anchor = GridBagConstraints.EAST;
        return g;
    }

    private JButton createSearchButton(String text, java.awt.event.ActionListener l) {
        JButton b = new JButton(text);
        b.addActionListener(l);
        return b;
    }

    private void showSearchResults(TreeSet<User> found, String criteria) {
        if (found.isEmpty()) {
            showResult("No Users found with " + criteria);
        } else {
            StringBuilder sb = new StringBuilder("Found " + found.size() + " User(s):\n");
            for (User w : found) {
                sb.append("  - ").append(w.toString()).append("\n");
            }
            showResult(sb.toString());
        }
    }

    private void showResult(String message) {
        resultArea.setText(message);
        resultArea.setCaretPosition(0);
    }
}


