package view;

import java.awt.*;
import javax.swing.*;
import model.User;
import model.CommunicationHub;

public class MainGUI extends JFrame {
    private final CommunicationHub CommunicationHub;
    private final User loggedInUser;

    private ChatListPanel chatListPanel;
    private ChatPanel chatPanel;
    private ConversationStore store;
    private GroupsStore groupsStore;

    public MainGUI(CommunicationHub CommunicationHub, User loggedInUser) {
        this.CommunicationHub = CommunicationHub;
        this.loggedInUser = loggedInUser;

        setTitle("CommunicationHub");
        setSize(1100, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        // Start maximized to better fit screen size while still having a reasonable base size
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);

        setLayout(new BorderLayout());
        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);

        setVisible(true);
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(7, 94, 84)); // WhatsApp-like teal
        header.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        JLabel title = new JLabel("CommunicationHub");
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));

        JLabel user = new JLabel("Logged in: " + loggedInUser.getFullName());
        user.setForeground(Color.WHITE);

        header.add(title, BorderLayout.WEST);
        header.add(user, BorderLayout.EAST);
        return header;
    }

    private JComponent buildBody() {
        store = new ConversationStore(loggedInUser);
        groupsStore = new GroupsStore(loggedInUser);
        chatPanel = new ChatPanel(store);
        chatPanel.setLoggedIn(loggedInUser);

        chatListPanel = new ChatListPanel(CommunicationHub, loggedInUser, selected -> {
            chatPanel.setContact(selected);
        });

        // Left side tabs: Chats, Contacts, Groups, Profile, and Operations
        JTabbedPane leftTabs = new JTabbedPane();
        leftTabs.addTab("Chats", chatListPanel);
        leftTabs.addTab("Contacts", new ContactsPanel(CommunicationHub, loggedInUser));
        leftTabs.addTab("Groups", new GroupsPanel(CommunicationHub, loggedInUser, groupsStore));
        leftTabs.addTab("Profile", new UserProfilePanel(CommunicationHub, loggedInUser));
        leftTabs.addTab("Search", new CommunicationHubOperationsPanel(CommunicationHub, loggedInUser));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                leftTabs,
                chatPanel);
        // Give a bit more space to the chat area while still keeping navigation visible
        split.setDividerLocation(360);
        split.setResizeWeight(0.25);
        split.setBorder(null);
        return split;
    }
}


