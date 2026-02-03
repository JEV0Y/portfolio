package view;

import javax.swing.*;
import model.PhoneNumber;
import model.User;
import model.CommunicationHub;
import java.awt.*;
import java.util.TreeSet;

public class ContactsPanel extends JPanel {
    private CommunicationHub CommunicationHub;
    private User loggedInUser;
    private JTextArea resultArea;
    private JList<String> contactsList;
    private DefaultListModel<String> contactsListModel;

    public ContactsPanel(CommunicationHub CommunicationHub, User loggedInUser) {
        this.CommunicationHub = CommunicationHub;
        this.loggedInUser = loggedInUser;
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBorder(BorderFactory.createTitledBorder("Contact Operations"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Add Contact
        JPanel addContactPanel = createPanelWithBorder("Add Contact");
        JTextField contactPhoneField = new JTextField();
        JButton addContactButton = new JButton("Add Contact");
        addContactButton.addActionListener(e -> {
            try {
                String phoneNumber = contactPhoneField.getText().trim();
                if (phoneNumber.isEmpty()) {
                    showResult("Error: Please enter a phone number");
                    return;
                }
                if (!PhoneNumber.isValid(phoneNumber)) {
                    showResult("Error: Invalid phone number format (use XXX-XXX-XXXX)");
                    return;
                }

                String[] parts = phoneNumber.split("-");
                User contactUser = CommunicationHub.findUser(new PhoneNumber(parts[0], parts[1], parts[2]));
                if (contactUser == null) {
                    showResult("Error: User with phone " + phoneNumber + " not found");
                    return;
                }

                if (loggedInUser.isAContact(contactUser)) {
                    showResult("Error: Already a contact");
                    return;
                }

                boolean success = loggedInUser.addContact(contactUser);
                showResult(success ? "Successfully added contact: " + contactUser.getFullName()
                        : "Failed to add contact");
                contactPhoneField.setText("");
                refreshContactsList();
            } catch (Exception ex) {
                showResult("Error: " + ex.getMessage());
            }
        });

        addContactPanel.add(new JLabel("Contact Phone:"), createGbc(0, 0, 0.3));
        addContactPanel.add(contactPhoneField, createGbc(1, 0, 0.7));
        addContactPanel.add(addContactButton, createGbc(1, 1, 0));

        controlPanel.add(addContactPanel, gbc);

        // Check if Contact by Name
        gbc.gridy++;
        JPanel checkNamePanel = createPanelWithBorder("Check Contact by Name");
        JTextField checkNameField = new JTextField();
        JButton checkNameButton = new JButton("Check");
        checkNameButton.addActionListener(e -> {
            try {
                String name = checkNameField.getText().trim();
                if (name.isEmpty()) {
                    showResult("Error: Please enter a name");
                    return;
                }
                boolean isContact = loggedInUser.isAContact(name);
                showResult(isContact ? name + " is in your contacts" : name + " is NOT in your contacts");
            } catch (Exception ex) {
                showResult("Error: " + ex.getMessage());
            }
        });

        checkNamePanel.add(new JLabel("Contact Name:"), createGbc(0, 0, 0.3));
        checkNamePanel.add(checkNameField, createGbc(1, 0, 0.7));
        checkNamePanel.add(checkNameButton, createGbc(1, 1, 0));

        controlPanel.add(checkNamePanel, gbc);

        // Check if Contact by Phone
        gbc.gridy++;
        JPanel checkPhonePanel = createPanelWithBorder("Check Contact by Phone");
        JTextField checkPhoneField = new JTextField();
        JButton checkPhoneButton = new JButton("Check");
        checkPhoneButton.addActionListener(e -> {
            try {
                String phoneNumber = checkPhoneField.getText().trim();
                if (phoneNumber.isEmpty()) {
                    showResult("Error: Please enter a phone number");
                    return;
                }
                if (!PhoneNumber.isValid(phoneNumber)) {
                    showResult("Error: Invalid phone number format");
                    return;
                }

                String[] parts = phoneNumber.split("-");
                boolean isContact = loggedInUser.isAContact(new PhoneNumber(parts[0], parts[1], parts[2]));
                showResult(isContact ? phoneNumber + " is in your contacts" : phoneNumber + " is NOT in your contacts");
            } catch (Exception ex) {
                showResult("Error: " + ex.getMessage());
            }
        });

        checkPhonePanel.add(new JLabel("Phone (XXX-XXX-XXXX):"), createGbc(0, 0, 0.3));
        checkPhonePanel.add(checkPhoneField, createGbc(1, 0, 0.7));
        checkPhonePanel.add(checkPhoneButton, createGbc(1, 1, 0));

        controlPanel.add(checkPhonePanel, gbc);

        // View All Contacts
        gbc.gridy++;
        JButton viewContactsButton = new JButton("Refresh Contact List");
        viewContactsButton.addActionListener(e -> refreshContactsList());
        controlPanel.add(viewContactsButton, gbc);

        JScrollPane scrollPane = new JScrollPane(controlPanel);
        add(scrollPane, BorderLayout.NORTH);

        // Contacts List Display
        contactsListModel = new DefaultListModel<>();
        contactsList = new JList<>(contactsListModel);
        contactsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane listScroll = new JScrollPane(contactsList);
        listScroll.setBorder(BorderFactory.createTitledBorder("Your Contacts"));
        add(listScroll, BorderLayout.CENTER);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resultArea.setBorder(BorderFactory.createTitledBorder("Operation Results"));
        JScrollPane resultScroll = new JScrollPane(resultArea);
        resultScroll.setPreferredSize(new Dimension(400, 100));
        add(resultScroll, BorderLayout.SOUTH);

        refreshContactsList();
    }

    private void refreshContactsList() {
        contactsListModel.clear();
        try {
            // Get all registered Users and filter to only those in contacts
            for (User w : CommunicationHub.getRegisteredUsers()) {
                if (loggedInUser.isAContact(w)) {
                    contactsListModel.addElement(w.getFullName() + "  [" + w.getPhoneNumber().toString() + "]");
                }
            }
            showResult("Contacts list refreshed (" + contactsListModel.size() + ")");
        } catch (Exception ex) {
            showResult("Error refreshing contacts: " + ex.getMessage());
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


