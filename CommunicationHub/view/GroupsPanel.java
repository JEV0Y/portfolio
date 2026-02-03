package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.User;
import model.CommunicationHub;
import model.PhoneNumber;
import model.enumerations.Action;
import model.groups.Group;
import java.awt.*;
import java.util.List;

/**
 * GroupsPanel provides a complete interface for managing groups.
 * Users can create groups, send posts, manage members, and perform admin
 * actions.
 *
 * Features:
 * - View all groups user is member/admin of
 * - Create new groups (Regular, UserToUser, MessagesWithSelf, Community)
 * - Send posts and replies within groups
 * - Add/remove members (admin only)
 * - Promote/demote members to/from admin (admin only)
 * - Leave groups
 * - Deactivate groups (admin only)
 * - View group profile with members and audit log
 */
public class GroupsPanel extends JPanel {

    private final CommunicationHub CommunicationHub;
    private final User loggedInUser;
    private final GroupsStore groupsStore;
    private final DefaultListModel<Group> groupsModel = new DefaultListModel<>();
    private final JList<Group> groupsList = new JList<>(groupsModel);
    private final JTextArea profileArea = new JTextArea();
    private final JButton newGroupBtn = new JButton("Ã¢Å¾â€¢ New Group");

    // Control fields for group actions
    private final JTextField postField = new JTextField();
    private final JTextField replyPostIdField = new JTextField();
    private final JTextField replyTextField = new JTextField();
    private final JTextField memberPhoneField = new JTextField();
    private final JLabel statusLabel = new JLabel(" ");

    private Group selectedGroup;

    /**
     * Constructs GroupsPanel with CommunicationHub instance, logged-in User, and groups
     * store.
     *
     * @param CommunicationHub   the CommunicationHub model instance
     * @param loggedInUser the currently logged-in User
     * @param groupsStore   persistent storage for groups
     */
    public GroupsPanel(CommunicationHub CommunicationHub, User loggedInUser, GroupsStore groupsStore) {
        this.CommunicationHub = CommunicationHub;
        this.loggedInUser = loggedInUser;
        this.groupsStore = groupsStore;
        setLayout(new BorderLayout());
        add(buildToolbar(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
        refreshList();
        wire();
    }

    /**
     * Builds the toolbar with title and "New Group" button.
     */
    private JComponent buildToolbar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBorder(new EmptyBorder(6, 6, 6, 6));
        bar.setBackground(new Color(7, 94, 84));

        JLabel title = new JLabel("Your Groups");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 14f));
        title.setForeground(Color.WHITE);
        bar.add(title, BorderLayout.WEST);

        newGroupBtn.setBackground(new Color(37, 211, 102));
        newGroupBtn.setForeground(Color.WHITE);
        newGroupBtn.setFocusPainted(false);
        bar.add(newGroupBtn, BorderLayout.EAST);

        return bar;
    }

    /**
     * Builds the main body with groups list and details panel.
     */
    private JComponent buildBody() {
        groupsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        groupsList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Group g) {
                    setText(g.getName());
                }
                return c;
            }
        });

        JScrollPane left = new JScrollPane(groupsList);
        left.setBorder(BorderFactory.createTitledBorder("Groups"));

        profileArea.setEditable(false);
        profileArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        JScrollPane profileScroll = new JScrollPane(profileArea);
        profileScroll.setBorder(BorderFactory.createTitledBorder("Group Profile & Posts"));

        JPanel rightPanel = new JPanel(new BorderLayout(4, 4));
        rightPanel.add(profileScroll, BorderLayout.CENTER);

        // Bottom area contains both the actions panel and the status bar
        JPanel bottomPanel = new JPanel(new BorderLayout(0, 0));
        bottomPanel.add(buildActionsPanel(), BorderLayout.CENTER);

        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(new EmptyBorder(4, 4, 4, 4));
        statusBar.add(statusLabel, BorderLayout.WEST);
        bottomPanel.add(statusBar, BorderLayout.SOUTH);

        rightPanel.add(bottomPanel, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, rightPanel);
        split.setDividerLocation(280);
        split.setResizeWeight(0.35);
        split.setBorder(null);

        return split;
    }

    /**
     * Wires event listeners for group selection and new group button.
     */
    private void wire() {
        newGroupBtn.addActionListener(e -> openNewGroupDialog());
        groupsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showProfile(groupsList.getSelectedValue());
            }
        });
    }

    /**
     * Opens the new group creation dialog.
     */
    private void openNewGroupDialog() {
        NewGroupDialog dlg = new NewGroupDialog(SwingUtilities.getWindowAncestor(this),
                CommunicationHub, loggedInUser);
        dlg.setVisible(true);
        Group g = dlg.getCreatedGroup();
        if (g != null) {
            groupsStore.add(g);
            refreshList();
            groupsList.setSelectedValue(g, true);
            showSuccess("Group created successfully!");
        }
    }

    /**
     * Refreshes the groups list from the store.
     */
    private void refreshList() {
        groupsModel.clear();
        List<Group> all = groupsStore.all();
        for (Group g : all) {
            groupsModel.addElement(g);
        }
        if (!groupsModel.isEmpty()) {
            groupsList.setSelectedIndex(0);
        }
    }

    /**
     * Displays the profile of the selected group.
     *
     * @param g the group to display, or null to clear
     */
    private void showProfile(Group g) {
        selectedGroup = g;
        if (g == null) {
            profileArea.setText("");
            statusLabel.setText(" ");
        } else {
            profileArea.setText(g.getFullInformation());
            profileArea.setCaretPosition(0);
            statusLabel.setText("Selected: " + g.getName());
        }
    }

    /**
     * Builds the actions panel with controls for posting, replying, and member
     * management.
     */
    /**
     * Builds the actions panel with control groups using GridBagLayout.
     */
    private JComponent buildActionsPanel() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(4, 4, 4, 4));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;

        // === POSTING ===
        JPanel postingPanel = new JPanel(new GridBagLayout());
        postingPanel.setBorder(BorderFactory.createTitledBorder("New Post"));
        GridBagConstraints pGbc = new GridBagConstraints();
        pGbc.insets = new Insets(4, 4, 4, 4);
        pGbc.fill = GridBagConstraints.HORIZONTAL;

        pGbc.gridx = 0;
        pGbc.gridy = 0;
        pGbc.weightx = 0;
        postingPanel.add(new JLabel("Text:"), pGbc);

        pGbc.gridx = 1;
        pGbc.weightx = 1.0;
        postingPanel.add(postField, pGbc);

        pGbc.gridx = 2;
        pGbc.weightx = 0;
        JButton postBtn = new JButton("Ã°Å¸â€œÂ¤ Post");
        postBtn.addActionListener(e -> doPost());
        postingPanel.add(postBtn, pGbc);

        gbc.gridy = 0;
        mainPanel.add(postingPanel, gbc);

        // === REPLYING ===
        JPanel replyPanel = new JPanel(new GridBagLayout());
        replyPanel.setBorder(BorderFactory.createTitledBorder("Reply to Post"));

        pGbc.gridx = 0;
        pGbc.gridy = 0;
        pGbc.weightx = 0;
        replyPanel.add(new JLabel("ID:"), pGbc);

        pGbc.gridx = 1;
        pGbc.weightx = 0.2;
        replyPanel.add(replyPostIdField, pGbc);

        pGbc.gridx = 2;
        pGbc.weightx = 0;
        replyPanel.add(new JLabel("Text:"), pGbc);

        pGbc.gridx = 3;
        pGbc.weightx = 0.8;
        replyPanel.add(replyTextField, pGbc);

        pGbc.gridx = 4;
        pGbc.weightx = 0;
        JButton replyBtn = new JButton("Ã°Å¸â€™Â¬ Reply");
        replyBtn.addActionListener(e -> doReply());
        replyPanel.add(replyBtn, pGbc);

        gbc.gridy = 1;
        mainPanel.add(replyPanel, gbc);

        // === MEMBER MANAGEMENT ===
        JPanel memberPanel = new JPanel(new GridBagLayout());
        memberPanel.setBorder(BorderFactory.createTitledBorder("Manage Members"));

        pGbc.gridx = 0;
        pGbc.gridy = 0;
        pGbc.weightx = 0;
        memberPanel.add(new JLabel("Phone:"), pGbc);

        pGbc.gridx = 1;
        pGbc.weightx = 1.0;
        memberPanel.add(memberPhoneField, pGbc);

        // Buttons row
        JPanel memberBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        JButton addMemberBtn = new JButton("Ã¢Å¾â€¢ Add");
        addMemberBtn.addActionListener(e -> doAddMember());
        JButton removeMemberBtn = new JButton("Ã¢Å¾â€“ Remove");
        removeMemberBtn.addActionListener(e -> doRemoveMember());
        JButton promoteBtn = new JButton("Ã¢Â¬â€  Promote");
        promoteBtn.addActionListener(e -> doPromoteToAdmin());
        JButton demoteBtn = new JButton("Ã¢Â¬â€¡ Demote");
        demoteBtn.addActionListener(e -> doDemoteToRegular());

        memberBtnPanel.add(addMemberBtn);
        memberBtnPanel.add(removeMemberBtn);
        memberBtnPanel.add(promoteBtn);
        memberBtnPanel.add(demoteBtn);

        pGbc.gridx = 0;
        pGbc.gridy = 1;
        pGbc.gridwidth = 2;
        memberPanel.add(memberBtnPanel, pGbc);

        gbc.gridy = 2;
        mainPanel.add(memberPanel, gbc);

        // === GROUP ACTIONS ===
        JPanel groupActionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton leaveBtn = new JButton("Ã°Å¸Å¡Âª Leave");
        leaveBtn.addActionListener(e -> doLeave());
        JButton deactivateBtn = new JButton("Ã°Å¸â€â€™ Deactivate");
        deactivateBtn.setBackground(new Color(220, 53, 69));
        deactivateBtn.setForeground(Color.WHITE);
        deactivateBtn.addActionListener(e -> doDeactivate());

        groupActionPanel.add(leaveBtn);
        groupActionPanel.add(deactivateBtn);

        gbc.gridy = 3;
        mainPanel.add(groupActionPanel, gbc);

        // Use a wrapper to push everything to the top
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(mainPanel, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder(BorderFactory.createTitledBorder("Group Actions"));
        return scroll;
    }

    /**
     * Posts a message to the selected group.
     */
    private void doPost() {
        if (selectedGroup == null) {
            showError("Select a group first");
            return;
        }

        String text = postField.getText().trim();
        if (text.isEmpty()) {
            showError("Post text cannot be empty");
            return;
        }

        boolean success = selectedGroup.addPost(loggedInUser, text);
        if (success) {
            postField.setText("");
            showProfile(selectedGroup);
            showSuccess("Post sent successfully");
        } else {
            showError("Failed to send post (access denied or constraint violation)");
        }
    }

    /**
     * Replies to a post in the selected group.
     */
    private void doReply() {
        if (selectedGroup == null) {
            showError("Select a group first");
            return;
        }

        String postIdStr = replyPostIdField.getText().trim();
        String replyText = replyTextField.getText().trim();

        if (postIdStr.isEmpty() || replyText.isEmpty()) {
            showError("Post ID and reply text are required");
            return;
        }

        try {
            long id = Long.parseLong(postIdStr);
            boolean success = selectedGroup.addReply(loggedInUser, id, replyText);
            if (success) {
                replyPostIdField.setText("");
                replyTextField.setText("");
                showProfile(selectedGroup);
                showSuccess("Reply sent successfully");
            } else {
                showError("Failed to reply (post not found or access denied)");
            }
        } catch (NumberFormatException e) {
            showError("Post ID must be a number");
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
        }
    }

    /**
     * Adds a member to the selected group (admin only).
     */
    private void doAddMember() {
        if (selectedGroup == null) {
            showError("Select a group first");
            return;
        }

        String phoneStr = memberPhoneField.getText().trim();
        if (!validatePhone(phoneStr)) {
            return;
        }

        User target = findUserByPhone(phoneStr);
        if (target == null) {
            showError("User not found");
            return;
        }

        boolean success = selectedGroup.addMember(loggedInUser, target);
        if (success) {
            memberPhoneField.setText("");
            showProfile(selectedGroup);
            showSuccess("Member added successfully");
        } else {
            showError("Failed to add member (already member, at capacity, or access denied)");
        }
    }

    /**
     * Removes a member from the selected group (admin only).
     */
    private void doRemoveMember() {
        if (selectedGroup == null) {
            showError("Select a group first");
            return;
        }

        String phoneStr = memberPhoneField.getText().trim();
        if (!validatePhone(phoneStr)) {
            return;
        }

        User target = findUserByPhone(phoneStr);
        if (target == null) {
            showError("User not found");
            return;
        }

        boolean success = selectedGroup.removeMember(loggedInUser, target);
        if (success) {
            memberPhoneField.setText("");
            showProfile(selectedGroup);
            showSuccess("Member removed successfully");
        } else {
            showError("Failed to remove member (not a member, access denied, or last admin)");
        }
    }

    /**
     * Promotes a member to admin in the selected group (admin only).
     */
    private void doPromoteToAdmin() {
        if (selectedGroup == null) {
            showError("Select a group first");
            return;
        }

        String phoneStr = memberPhoneField.getText().trim();
        if (!validatePhone(phoneStr)) {
            return;
        }

        User target = findUserByPhone(phoneStr);
        if (target == null) {
            showError("User not found");
            return;
        }

        boolean success = selectedGroup.upgradeMemberToAdmin(loggedInUser, target);
        if (success) {
            showProfile(selectedGroup);
            showSuccess("Member promoted to admin");
        } else {
            showError("Failed to promote (already admin, not a member, or access denied)");
        }
    }

    /**
     * Demotes an admin to regular member in the selected group (admin only).
     */
    private void doDemoteToRegular() {
        if (selectedGroup == null) {
            showError("Select a group first");
            return;
        }

        String phoneStr = memberPhoneField.getText().trim();
        if (!validatePhone(phoneStr)) {
            return;
        }

        User target = findUserByPhone(phoneStr);
        if (target == null) {
            showError("User not found");
            return;
        }

        boolean success = selectedGroup.downgradeAdminToRegularMember(loggedInUser, target);
        if (success) {
            showProfile(selectedGroup);
            showSuccess("Admin demoted to regular member");
        } else {
            showError("Failed to demote (not an admin, must have one admin left, or access denied)");
        }
    }

    /**
     * Leaves the selected group (member only).
     */
    private void doLeave() {
        if (selectedGroup == null) {
            showError("Select a group first");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to leave this group?",
                "Leave Group",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        boolean success = loggedInUser.doGroupAction(Action.Leave, selectedGroup);
        if (success) {
            refreshList();
            showSuccess("You left the group");
        } else {
            showError("Failed to leave group (may be creator or last admin)");
        }
    }

    /**
     * Deactivates the selected group (admin only - permanent action).
     */
    private void doDeactivate() {
        if (selectedGroup == null) {
            showError("Select a group first");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Deactivate this group?\n\nÃ¢Å¡Â  WARNING: This action is PERMANENT and cannot be undone!",
                "Deactivate Group",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        boolean success = selectedGroup.deactivateGroup(loggedInUser);
        if (success) {
            showProfile(selectedGroup);
            refreshList();
            showSuccess("Group deactivated permanently");
        } else {
            showError("Failed to deactivate (admin only)");
        }
    }

    /**
     * Helper: Validates phone number format and displays error if invalid.
     *
     * @param phoneStr the phone string to validate
     * @return true if valid, false otherwise
     */
    private boolean validatePhone(String phoneStr) {
        if (phoneStr.isEmpty()) {
            showError("Phone number required");
            return false;
        }
        if (!PhoneNumber.isValid(phoneStr)) {
            showError("Invalid phone format (use XXX-XXX-XXXX)");
            return false;
        }
        return true;
    }

    /**
     * Helper: Finds a User by phone number.
     *
     * @param phoneStr the phone string in XXX-XXX-XXXX format
     * @return the User or null if not found
     */
    private User findUserByPhone(String phoneStr) {
        String[] parts = phoneStr.split("-");
        return CommunicationHub.findUser(new PhoneNumber(parts[0], parts[1], parts[2]));
    }

    /**
     * Shows error message in status label.
     */
    private void showError(String message) {
        statusLabel.setText("Ã¢ÂÅ’ " + message);
        statusLabel.setForeground(new Color(220, 53, 69));
    }

    /**
     * Shows success message in status label.
     */
    private void showSuccess(String message) {
        statusLabel.setText("Ã¢Å“â€œ " + message);
        statusLabel.setForeground(new Color(37, 211, 102));
    }

    /**
     * Shows info message in status label.
     */
    @SuppressWarnings("unused")
    private void showInfo(String message) {
        statusLabel.setText("Ã¢â€žÂ¹ " + message);
        statusLabel.setForeground(new Color(0, 123, 255));
    }
}


