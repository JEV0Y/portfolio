package view;

import model.User;
import model.CommunicationHub;
import model.groups.Group;
import model.enumerations.GroupType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class NewGroupDialog extends JDialog {
    private final CommunicationHub wsa;
    private final User me;

    private final JTextField nameField = new JTextField();
    private final JComboBox<GroupType> typeBox = new JComboBox<>(GroupType.values());
    private final JSpinner capacitySpinner = new JSpinner(new SpinnerNumberModel(3, 1, 5, 1));
    private final DefaultListModel<User> usersModel = new DefaultListModel<>();
    private final JList<User> usersList = new JList<>(usersModel);

    private Group createdGroup;

    public NewGroupDialog(Window owner, CommunicationHub wsa, User me) {
        super(owner, "Create Group", ModalityType.APPLICATION_MODAL);
        this.wsa = wsa;
        this.me = me;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        add(buildForm(), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);

        setSize(520, 520);
        setLocationRelativeTo(owner);
        populateUsers();
        updateControlsForType((GroupType) typeBox.getSelectedItem());
        typeBox.addActionListener(e -> updateControlsForType((GroupType) typeBox.getSelectedItem()));
    }

    private JComponent buildForm() {
        JPanel root = new JPanel(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 0;
        int r = 0;

        // Name
        gc.gridx = 0; gc.gridy = r; form.add(new JLabel("Name"), gc);
        gc.gridx = 1; gc.gridy = r; gc.weightx = 1; form.add(nameField, gc); gc.weightx = 0; r++;
        // Type
        gc.gridx = 0; gc.gridy = r; form.add(new JLabel("Type"), gc);
        gc.gridx = 1; gc.gridy = r; gc.weightx = 1; form.add(typeBox, gc); gc.weightx = 0; r++;
        // Capacity
        gc.gridx = 0; gc.gridy = r; form.add(new JLabel("Capacity"), gc);
        gc.gridx = 1; gc.gridy = r; gc.weightx = 1; form.add(capacitySpinner, gc); gc.weightx = 0; r++;

        root.add(form, BorderLayout.NORTH);

        usersList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        usersList.setVisibleRowCount(10);
        // Render each row with a checkbox look-and-feel
        usersList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JCheckBox box = new JCheckBox();
            if (value instanceof User w) {
                box.setText(w.getFullName() + "  [" + w.getPhoneNumber() + "]");
            } else if (value != null) {
                box.setText(value.toString());
            }
            box.setSelected(isSelected);
            box.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            box.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
            return box;
        });
        // Custom selection model: clicking an item toggles its selection without
        // requiring Ctrl. This overrides the default behavior that always clears
        // and re-selects on plain click.
        usersList.setSelectionModel(new DefaultListSelectionModel() {
            @Override
            public void setSelectionInterval(int index0, int index1) {
                if (isSelectedIndex(index0)) {
                    removeSelectionInterval(index0, index1);
                } else {
                    addSelectionInterval(index0, index1);
                }
            }
        });
        JScrollPane scroll = new JScrollPane(usersList);
        scroll.setBorder(BorderFactory.createTitledBorder("Select members"));
        root.add(scroll, BorderLayout.CENTER);

        JLabel hint = new JLabel("Hint: User-to-User requires 1 member; Messages-to-Self requires 0.");
        hint.setBorder(new EmptyBorder(0, 6, 0, 6));
        root.add(hint, BorderLayout.SOUTH);

        return root;
    }

    private JComponent buildButtons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = new JButton("Cancel");
        JButton ok = new JButton("Create");
        p.add(cancel);
        p.add(ok);

        cancel.addActionListener(e -> { createdGroup = null; dispose(); });
        ok.addActionListener(e -> onCreate());
        getRootPane().setDefaultButton(ok);
        return p;
    }

    private void populateUsers() {
        usersModel.clear();
        TreeSet<User> all = wsa.getRegisteredUsers();
        for (User w : all) if (me == null || w.getPhoneNumber().compareTo(me.getPhoneNumber()) != 0) usersModel.addElement(w);
    }

    private void updateControlsForType(GroupType type) {
        if (type == null) return;
        switch (type) {
            case MessagesToSelf -> {
                capacitySpinner.setValue(1);
                capacitySpinner.setEnabled(false);
                usersList.setEnabled(false);
                nameField.setEnabled(false);
            }
            case UsertoUser -> {
                capacitySpinner.setValue(2);
                capacitySpinner.setEnabled(false);
                usersList.setEnabled(true);
                nameField.setEnabled(false);
            }
            default -> {
                capacitySpinner.setEnabled(true);
                usersList.setEnabled(true);
                nameField.setEnabled(true);
            }
        }
    }

    private void onCreate() {
        GroupType type = (GroupType) typeBox.getSelectedItem();
        String name = nameField.getText().trim();
        int cap = (Integer) capacitySpinner.getValue();

        if (type == GroupType.RegularGroup && name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a group name.", "Create Group", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<User> selected = usersList.getSelectedValuesList();
        if (type == GroupType.UsertoUser && selected.size() != 1) {
            JOptionPane.showMessageDialog(this, "Select exactly 1 member for a private chat.", "Create Group", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (type == GroupType.MessagesToSelf && !selected.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Messages-to-Self requires no additional members.", "Create Group", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Set<User> members = new HashSet<>(selected);
        try {
            createdGroup = new Group(me, type, name, (short) cap, members);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to create group: " + ex.getMessage(), "Create Group", JOptionPane.ERROR_MESSAGE);
            createdGroup = null;
        }
        dispose();
    }

    public Group getCreatedGroup() { return createdGroup; }
}


