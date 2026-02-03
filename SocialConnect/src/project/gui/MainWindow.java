package project.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;
import project.*;
import project.enums.PostAudience;

/**
 * Main window for the SocialConnect application.
 * Handles user login and navigation to different features.
 */
public class MainWindow extends JFrame {
    private SocialConnect SocialConnect;
    private JPanel mainPanel;
    private JPanel loginPanel;
    private JPanel userPanel;
    private User currentUser;

    // Login components
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel statusLabel;

    /**
     * Creates the main window for the SocialConnect application.
     * 
     * @param SocialConnect The SocialConnect platform instance
     */
    public MainWindow(SocialConnect SocialConnect) {
        this.SocialConnect = SocialConnect;
        setupWindow();
        createLoginPanel();
        createUserPanel();
        showLoginPanel();
    }

    private void setupWindow() {
        setTitle("SocialConnect Platform");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        mainPanel = new JPanel(new CardLayout());
        add(mainPanel);
    }

    private void createLoginPanel() {
        loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Username field
        gbc.gridx = 0;
        gbc.gridy = 0;
        loginPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(20);
        loginPanel.add(usernameField, gbc);

        // Password field
        gbc.gridx = 0;
        gbc.gridy = 1;
        loginPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        loginPanel.add(passwordField, gbc);

        // Login button
        gbc.gridx = 1;
        gbc.gridy = 2;
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> handleLogin());
        loginPanel.add(loginButton, gbc);

        // Status label
        gbc.gridx = 1;
        gbc.gridy = 3;
        statusLabel = new JLabel(" ");
        loginPanel.add(statusLabel, gbc);

        mainPanel.add(loginPanel, "login");
    }

    private void createUserPanel() {
        userPanel = new JPanel(new BorderLayout());

        // Create menu bar
        JMenuBar menuBar = new JMenuBar();

        // Posts menu
        JMenu postsMenu = new JMenu("Posts");
        JMenuItem createTextPost = new JMenuItem("Create Text Post");
        createTextPost.addActionListener(e -> showCreatePostDialog());
        postsMenu.add(createTextPost);

        JMenuItem createImagePost = new JMenuItem("Create Image Post");
        createImagePost.addActionListener(e -> showCreateImagePostDialog());
        postsMenu.add(createImagePost);

        JMenuItem viewPosts = new JMenuItem("View Posts");
        viewPosts.addActionListener(e -> showPostsPanel());
        postsMenu.add(viewPosts);

        // Users menu
        JMenu usersMenu = new JMenu("Users");
        JMenuItem findUsers = new JMenuItem("Find Users");
        findUsers.addActionListener(e -> showFindUsersPanel());
        usersMenu.add(findUsers);

        JMenuItem followers = new JMenuItem("Followers");
        followers.addActionListener(e -> showFollowersPanel());
        usersMenu.add(followers);

        // Add menus to menu bar
        menuBar.add(postsMenu);
        menuBar.add(usersMenu);

        // Logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> handleLogout());
        menuBar.add(logoutButton);

        userPanel.add(menuBar, BorderLayout.NORTH);

        // Welcome panel
        JPanel welcomePanel = new JPanel(new GridBagLayout());
        welcomePanel.add(new JLabel("Welcome to SocialConnect!"));
        userPanel.add(welcomePanel, BorderLayout.CENTER);

        mainPanel.add(userPanel, "user");
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try {
            if (SocialConnect.login(username, password)) {
                currentUser = SocialConnect.getWhoIsLoggedIn();
                showUserPanel();
                statusLabel.setText("");
            } else {
                statusLabel.setText("Invalid username or password");
                statusLabel.setForeground(Color.RED);
            }
        } catch (IllegalArgumentException e) {
            statusLabel.setText(e.getMessage());
            statusLabel.setForeground(Color.RED);
        }
    }

    private void handleLogout() {
        SocialConnect.logout();
        currentUser = null;
        showLoginPanel();
        usernameField.setText("");
        passwordField.setText("");
    }

    private void showLoginPanel() {
        CardLayout cl = (CardLayout) mainPanel.getLayout();
        cl.show(mainPanel, "login");
    }

    private void showUserPanel() {
        CardLayout cl = (CardLayout) mainPanel.getLayout();
        cl.show(mainPanel, "user");
    }

    private void showCreatePostDialog() {
        JDialog dialog = new JDialog(this, "Create Post", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Text area for post content
        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel("Post Content:"), gbc);
        gbc.gridx = 1;
        JTextArea contentArea = new JTextArea(5, 20);
        dialog.add(new JScrollPane(contentArea), gbc);

        // Speech recognition button
        gbc.gridx = 2;
        gbc.gridy = 0;
        JButton speechButton = new JButton("Record Speech");
        speechButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(dialog, "Speech recognition is currently unavailable.", "Feature Disabled",
                    JOptionPane.INFORMATION_MESSAGE);
        });
        dialog.add(speechButton, gbc);

        // Audience selection
        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(new JLabel("Audience:"), gbc);
        gbc.gridx = 1;
        JComboBox<PostAudience> audienceBox = new JComboBox<>(PostAudience.values());
        dialog.add(audienceBox, gbc);

        // Create button
        gbc.gridx = 1;
        gbc.gridy = 2;
        JButton createButton = new JButton("Create Post");
        createButton.addActionListener(e -> {
            try {
                currentUser.createPost(
                        contentArea.getText(),
                        (PostAudience) audienceBox.getSelectedItem());
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Post created successfully!");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.add(createButton, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showCreateImagePostDialog() {
        JDialog dialog = new JDialog(this, "Create Image Post", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Text caption for the image
        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel("Caption:"), gbc);
        gbc.gridx = 1;
        JTextArea captionArea = new JTextArea(3, 20);
        dialog.add(new JScrollPane(captionArea), gbc);

        // Image file selection
        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(new JLabel("Image:"), gbc);
        gbc.gridx = 1;
        JPanel imagePanel = new JPanel(new BorderLayout());
        JTextField imagePathField = new JTextField(15);
        imagePathField.setEditable(false);
        imagePanel.add(imagePathField, BorderLayout.CENTER);

        JButton browseButton = new JButton("Browse");
        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter(
                    "Image files", "jpg", "jpeg", "png", "gif"));

            if (fileChooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                imagePathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });
        imagePanel.add(browseButton, BorderLayout.EAST);
        dialog.add(imagePanel, gbc);

        // Audience selection
        gbc.gridx = 0;
        gbc.gridy = 2;
        dialog.add(new JLabel("Audience:"), gbc);
        gbc.gridx = 1;
        JComboBox<PostAudience> audienceBox = new JComboBox<>(PostAudience.values());
        dialog.add(audienceBox, gbc);

        // Preview panel for the selected image
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JLabel previewLabel = new JLabel();
        previewLabel.setPreferredSize(new Dimension(200, 200));
        previewLabel.setBorder(BorderFactory.createTitledBorder("Image Preview"));
        dialog.add(previewLabel, gbc);

        // Update preview when image is selected
        imagePathField.addPropertyChangeListener("text", e -> {
            String path = imagePathField.getText();
            if (!path.isEmpty()) {
                try {
                    ImageIcon icon = new ImageIcon(path);
                    Image img = icon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
                    previewLabel.setIcon(new ImageIcon(img));
                } catch (Exception ex) {
                    previewLabel.setIcon(null);
                    previewLabel.setText("Error loading image");
                }
            } else {
                previewLabel.setIcon(null);
                previewLabel.setText("");
            }
        });

        // Create button
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JButton createButton = new JButton("Create Image Post");
        createButton.addActionListener(e -> {
            try {
                String imagePath = imagePathField.getText();
                if (imagePath.isEmpty()) {
                    throw new IllegalArgumentException("Please select an image");
                }

                currentUser.createImagePost(
                        captionArea.getText(),
                        imagePath,
                        (PostAudience) audienceBox.getSelectedItem());
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Image post created successfully!");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.add(createButton, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showPostsPanel() {
        // Implementation for viewing posts
        JOptionPane.showMessageDialog(this, "Posts panel coming soon!");
    }

    private void showFindUsersPanel() {
        // Implementation for finding users
        JOptionPane.showMessageDialog(this, "Find users panel coming soon!");
    }

    private void showFollowersPanel() {
        // Implementation for viewing followers
        JOptionPane.showMessageDialog(this, "Followers panel coming soon!");
    }
}
