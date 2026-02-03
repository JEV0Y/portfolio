package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import model.User;

public class ChatPanel extends JPanel {
    private final JPanel header = new JPanel(new BorderLayout());
    private final JLabel contactName = new JLabel("Select a chat");

    private final JPanel messagesPanel = new JPanel();
    private final JTextField composer = new JTextField();
    private final JButton sendBtn = new JButton("Send");
    private final JButton simulateBtn = new JButton("Simulate Reply");

    private User me;
    private User contact;
    private final ConversationStore store;

    public ChatPanel(ConversationStore store) {
        this.store = store;
        setLayout(new BorderLayout());
        add(buildHeader(), BorderLayout.NORTH);
        add(buildMessages(), BorderLayout.CENTER);
        add(buildComposer(), BorderLayout.SOUTH);
    }

    private JComponent buildHeader() {
        header.setBackground(new Color(7, 94, 84));
        header.setBorder(new EmptyBorder(10, 16, 10, 16));
        contactName.setForeground(Color.WHITE);
        contactName.setFont(contactName.getFont().deriveFont(Font.BOLD, 14f));
        header.add(contactName, BorderLayout.WEST);
        return header;
    }

    private JComponent buildMessages() {
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        messagesPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        messagesPanel.setBackground(Color.WHITE);
        JScrollPane sp = new JScrollPane(messagesPanel);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(230,230,230)));
        return sp;
    }

    private JComponent buildComposer() {
        JPanel bar = new JPanel(new BorderLayout(8, 8));
        bar.setBorder(new EmptyBorder(8, 8, 8, 8));
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        right.add(simulateBtn);
        right.add(sendBtn);
        bar.add(composer, BorderLayout.CENTER);
        bar.add(right, BorderLayout.EAST);

        sendBtn.addActionListener(e -> sendMessage());
        simulateBtn.addActionListener(e -> simulateIncoming());
        composer.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !e.isShiftDown()) {
                    e.consume();
                    sendMessage();
                }
            }
        });
        return bar;
    }

    private void sendMessage() {
        String text = composer.getText().trim();
        if (text.isEmpty() || contact == null) return;
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        if (store != null && me != null) {
            store.appendOutgoing(me, contact, text, time);
        }
        appendBubble((me != null ? me.getFirstname() : "Me") + " (" + time + ")", text, true);
        composer.setText("");
    }

    private void appendBubble(String header, String text, boolean outgoing) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);

        JPanel bubble = new JPanel();
        bubble.setLayout(new BoxLayout(bubble, BoxLayout.Y_AXIS));
        bubble.setBorder(new EmptyBorder(6, 10, 6, 10));
        bubble.setBackground(outgoing ? new Color(220, 248, 198) : new Color(240, 242, 245));
        bubble.setOpaque(true);

        JLabel h = new JLabel(header);
        h.setFont(h.getFont().deriveFont(Font.BOLD, 11f));
        JTextArea body = new JTextArea(text);
        body.setLineWrap(true);
        body.setWrapStyleWord(true);
        body.setEditable(false);
        body.setOpaque(false);

        bubble.add(h);
        bubble.add(Box.createVerticalStrut(4));
        bubble.add(body);

        if (outgoing) {
            row.add(bubble, BorderLayout.EAST);
        } else {
            row.add(bubble, BorderLayout.WEST);
        }

        messagesPanel.add(row);
        messagesPanel.add(Box.createVerticalStrut(6));
        messagesPanel.revalidate();
        SwingUtilities.invokeLater(() -> {
            Container c = messagesPanel.getParent();
            if (c instanceof JViewport vp) {
                JComponent parent = (JComponent) vp.getView();
                vp.setViewPosition(new Point(0, parent.getHeight()));
            }
        });
    }

    private void simulateIncoming() {
        if (contact == null) return;
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        String text = "Auto-reply from " + contact.getFirstname();
        if (store != null) store.appendIncoming(contact, text, time);
        appendBubble(contact.getFirstname() + " (" + time + ")", text, false);
    }

    public void setLoggedIn(User me) {
        this.me = me;
    }

    public void setContact(User contact) {
        this.contact = contact;
        messagesPanel.removeAll();
        if (contact == null) {
            contactName.setText("Select a chat");
        } else {
            contactName.setText(contact.getFullName() + "  [" + contact.getPhoneNumber().toString() + "]");
            appendBubble("Chat", "with " + contact.getFullName(), false);
            if (store != null) {
                List<String> history = store.getHistory(contact);
                for (String line : history) {
                    boolean outgoing = line.startsWith("> ");
                    boolean incoming = line.startsWith("< ");
                    String display = line;
                    if (outgoing || incoming) display = line.substring(2);
                    int sep = display.indexOf("):");
                    String header = sep > 0 ? display.substring(0, sep+1) : "";
                    String body = sep > 0 ? display.substring(sep+3) : display;
                    appendBubble(header, body, outgoing);
                }
            }
        }
    }
}

