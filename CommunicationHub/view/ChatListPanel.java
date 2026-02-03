package view;

import model.User;
import model.CommunicationHub;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.TreeSet;
import java.util.function.Consumer;

public class ChatListPanel extends JPanel {
    private final CommunicationHub wsa;
    private final Consumer<User> onSelect;
    private final User me;

    private final JTextField searchField = new JTextField();
    private final JComboBox<String> searchMode = new JComboBox<>(new String[]{
            "Name (contains)", "Phone prefix", "Phone contains", "Phone postfix"
    });
    private final JButton searchBtn = new JButton("Search");

    private final DefaultListModel<User> listModel = new DefaultListModel<>();
    private final JList<User> list = new JList<>(listModel);

    public ChatListPanel(CommunicationHub wsa, User me, Consumer<User> onSelect) {
        this.wsa = wsa;
        this.me = me;
        this.onSelect = onSelect;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(0,0,0,0));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildList(), BorderLayout.CENTER);
        wire();
        populateAll();
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout(8, 8));
        header.setBorder(new EmptyBorder(8,8,8,8));

        JPanel top = new JPanel(new BorderLayout(8, 8));
        JTextField placeholder = searchField;
        placeholder.putClientProperty("JTextField.placeholderText", "Search or start new chat");
        top.add(placeholder, BorderLayout.CENTER);
        top.add(searchMode, BorderLayout.EAST);

        header.add(top, BorderLayout.CENTER);
        header.add(searchBtn, BorderLayout.EAST);
        return header;
    }

    private JComponent buildList() {
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof User w) {
                    setText(w.getFullName() + "  [" + w.getPhoneNumber().toString() + "]");
                }
                return c;
            }
        });
        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(BorderFactory.createMatteBorder(1,0,0,1,new Color(230,230,230)));
        return scroll;
    }

    private void wire() {
        searchBtn.addActionListener(this::doSearch);
        searchField.addActionListener(this::doSearch);
        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && onSelect != null) {
                onSelect.accept(list.getSelectedValue());
            }
        });
    }

    private void doSearch(ActionEvent e) {
        listModel.clear();
        String q = searchField.getText() == null ? "" : searchField.getText().trim();
        if (q.isEmpty()) return;

        TreeSet<User> found = new TreeSet<>();
        String mode = (String) searchMode.getSelectedItem();
        if ("Name (contains)".equals(mode)) {
            // Allow searching by first name, last name, or any substring of the full name
            found = wsa.findUsersWithNameSubString(q);
        } else if ("Phone prefix".equals(mode)) {
            found = wsa.findUserWithPhonePrefix(q);
        } else if ("Phone contains".equals(mode)) {
            found = wsa.findUserWithPhoneSubString(q);
        } else if ("Phone postfix".equals(mode)) {
            found = wsa.findUserWithPhonePostfix(q);
        }
        for (User w : found) listModel.addElement(w);
        if (!listModel.isEmpty()) list.setSelectedIndex(0);
    }

    private void populateAll() {
        listModel.clear();
        TreeSet<User> all = wsa.getRegisteredUsers();
        for (User w : all) {
            if (me != null && w.getPhoneNumber().compareTo(me.getPhoneNumber()) == 0) continue;
            listModel.addElement(w);
        }
        if (!listModel.isEmpty()) list.setSelectedIndex(0);
    }
}


