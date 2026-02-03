/**
 * @author 
 *
 * This class represents the graphical user interface (GUI) of the SocialConnect application.
 * It provides a complete user interface for interacting with the social media platform,
 * including user authentication, post management, and social interactions.
 *
 * Features include:
 * - User registration and login
 * - Post creation and sharing
 * - User profile management
 * - Follow/unfollow functionality
 * - Feed viewing with different sorting algorithms
 * - Interactive post engagement (upvotes/downvotes)
 *
 * The GUI is built using Swing components and follows a panel-based architecture
 * where different screens are swapped in and out of the main frame.
 */
package project.gui;

import project.Post;
import project.RegularPost;
import project.ResharedPost;
import project.ImagePost;
import project.SocialConnect;
import project.User;
import project.enums.PageAlgorithm;
import project.enums.PostAudience;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * This class represents the graphical user interface (GUI) of the SocialConnect
 * application.
 * It provides methods for creating different panels for user login,
 * registration, dashboard, post creation, and follow management.
 */
public class SocialConnectGUI {
    private SocialConnect SocialConnect;
    private JFrame frame;
    private JTextField usernameField; // Changed from TextField to JTextField
    private JPasswordField passwordField;
    private JLabel userLabel;
    private User defaultUser1;
    private User defaultUser2;
    private User defaultUser3;
    private User defaultUser4;
    private JPanel panel;
    private JButton backButton;
    private ArrayList<Post> posts; // List to store all posts

    /**
     * Constructs a new SocialConnectGUI instance.
     * 
     * @param SocialConnect The SocialConnect instance to use
     */
    public SocialConnectGUI(SocialConnect SocialConnect) {
        this.SocialConnect = SocialConnect;
        this.frame = new JFrame("SocialConnect");
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setSize(800, 600);
        this.frame.setLayout(new BorderLayout());
        this.posts = new ArrayList<>();

        frame.getContentPane().setBackground(Color.WHITE);

        passwordField = new JPasswordField();

        // Create default users with usernames at least 8 characters long
        defaultUser1 = new User("alice1234", "password123");
        defaultUser2 = new User("bobsmith89", "password456");
        defaultUser3 = new User("charlie123", "password789");
        defaultUser4 = new User("diana12345", "password012");

        // Add default users to SocialConnect
        SocialConnect.addUser(defaultUser1);
        SocialConnect.addUser(defaultUser2);
        SocialConnect.addUser(defaultUser3);
        SocialConnect.addUser(defaultUser4);

        // Create and add default posts for testing
        SocialConnect
                .logNewPost(defaultUser1.createPost("Hello everyone! This is my first post.", PostAudience.Members));
        SocialConnect.logNewPost(defaultUser1.createPost("This post is for followers only.", PostAudience.Followers));
        SocialConnect.logNewPost(defaultUser2.createPost("Hi, I'm Bob! Nice to meet you all.", PostAudience.Members));
        SocialConnect.logNewPost(defaultUser3.createPost("Hello from Charlie!", PostAudience.Members));
        SocialConnect.logNewPost(
                defaultUser4.createPost("Diana here! Looking forward to socializing!", PostAudience.Members));

        // Create panels for different functionalities
        createLoginPanel();
        frame.setVisible(true);
    }

    /**
     * Creates a login panel and adds it to the frame.
     * This panel contains the components required for user login,
     * such as username and password fields, and a login button.
     */
    private void createLoginPanel() {
        panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        frame.add(panel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("SocialConnect Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // Username
        JLabel userLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(userLabel, gbc);

        JTextField userText = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(userText, gbc);

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(passwordLabel, gbc);

        JPasswordField passwordText = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(passwordText, gbc);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(0, 123, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        buttonPanel.add(loginButton);

        JButton createAccountButton = new JButton("Create Account");
        createAccountButton.setBackground(new Color(255, 69, 0));
        createAccountButton.setForeground(Color.WHITE);
        createAccountButton.setFocusPainted(false);
        buttonPanel.add(createAccountButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(buttonPanel, gbc);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userText.getText();
                String password = new String(passwordText.getPassword());
                if (SocialConnect.login(username, password)) {
                    JOptionPane.showMessageDialog(panel, "Login successful!");
                    frame.remove(panel);
                    createDashboardPanel();
                    frame.revalidate();
                    frame.repaint();
                } else {
                    JOptionPane.showMessageDialog(panel, "Login failed. Please check your username and password.");
                }
            }
        });

        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.remove(panel);
                createRegistrationPanel();
                frame.revalidate();
                frame.repaint();
            }
        });
    }

    private void createRegistrationPanel() {
        panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        frame.add(panel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // Username
        JLabel usernameLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(usernameLabel, gbc);

        JTextField usernameText = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(usernameText, gbc);

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(passwordLabel, gbc);

        JPasswordField passwordText = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(passwordText, gbc);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton registerButton = new JButton("Register");
        registerButton.setBackground(new Color(0, 123, 255));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        buttonPanel.add(registerButton);

        backButton = new JButton("Back to Login");
        backButton.setBackground(new Color(255, 69, 0));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        buttonPanel.add(backButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(buttonPanel, gbc);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameText.getText();
                String password = new String(passwordText.getPassword());

                StringBuilder errorMessage = new StringBuilder("Please fix the following errors:\n");
                boolean hasError = false;

                if (username.length() < 8) {
                    errorMessage.append("- Username must be at least 8 characters long.");
                    hasError = true;
                }
                if (password.length() < 8) {
                    errorMessage.append("\n- Password must be at least 8 characters long.");
                    hasError = true;
                }

                if (hasError) {
                    JOptionPane.showMessageDialog(panel, errorMessage.toString());
                    return;
                }

                if (SocialConnect.register(username, password)) {
                    JOptionPane.showMessageDialog(panel, "Registration successful!");
                    frame.getContentPane().removeAll();
                    createLoginPanel();
                    frame.revalidate();
                    frame.repaint();
                } else {
                    JOptionPane.showMessageDialog(panel, "Registration failed. Please choose a different username.");
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.getContentPane().removeAll();
                createLoginPanel();
                frame.revalidate();
                frame.repaint();
            }
        });
    }

    private void createDashboardPanel() {
        panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        frame.add(panel);

        // Header Panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(Color.WHITE);
        JLabel welcomeLabel = new JLabel("Welcome, " + SocialConnect.getWhoIsLoggedIn().getUsername() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(welcomeLabel);
        panel.add(headerPanel, BorderLayout.NORTH);

        // Control Panel (Left Side)
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBackground(Color.WHITE);
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Sorting Controls
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sortPanel.setBackground(Color.WHITE);
        JLabel sortLabel = new JLabel("Sort by:");
        JComboBox<PageAlgorithm> sortComboBox = new JComboBox<>(PageAlgorithm.values());
        sortComboBox.setSelectedItem(SocialConnect.getSortBy());
        sortPanel.add(sortLabel);
        sortPanel.add(sortComboBox);
        controlPanel.add(sortPanel);

        // Action Buttons
        JButton createPostButton = new JButton("Create Text Post");
        createPostButton.setBackground(new Color(0, 123, 255));
        createPostButton.setForeground(Color.WHITE);
        createPostButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(createPostButton);

        JButton createImagePostButton = new JButton("Create Image Post");
        createImagePostButton.setBackground(new Color(0, 123, 255));
        createImagePostButton.setForeground(Color.WHITE);
        createImagePostButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(createImagePostButton);

        JButton viewMyPostsButton = new JButton("View My Posts");
        viewMyPostsButton.setBackground(new Color(40, 167, 69));
        viewMyPostsButton.setForeground(Color.WHITE);
        viewMyPostsButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(viewMyPostsButton);

        JButton manageFollowsButton = new JButton("Manage Follows");
        manageFollowsButton.setBackground(new Color(108, 117, 125));
        manageFollowsButton.setForeground(Color.WHITE);
        manageFollowsButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(manageFollowsButton);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(logoutButton);

        panel.add(controlPanel, BorderLayout.WEST);

        // Posts Panel (Center)
        JPanel postsPanel = new JPanel();
        postsPanel.setLayout(new BoxLayout(postsPanel, BoxLayout.Y_AXIS));
        postsPanel.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(postsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Display initial posts
        displayPosts(postsPanel, (PageAlgorithm) sortComboBox.getSelectedItem());

        // Add listeners
        sortComboBox.addActionListener(e -> {
            PageAlgorithm selectedAlgorithm = (PageAlgorithm) sortComboBox.getSelectedItem();
            SocialConnect.setSortBy(selectedAlgorithm);
            refreshDashboard();
        });

        createPostButton.addActionListener(e -> {
            frame.remove(panel);
            createPostPanel();
            frame.revalidate();
            frame.repaint();
        });

        createImagePostButton.addActionListener(e -> {
            frame.remove(panel);
            showImagePostPanel();
            frame.revalidate();
            frame.repaint();
        });

        viewMyPostsButton.addActionListener(e -> {
            frame.remove(panel);
            createMyPostsPanel();
            frame.revalidate();
            frame.repaint();
        });

        manageFollowsButton.addActionListener(e -> {
            frame.remove(panel);
            createFollowManagement();
            frame.revalidate();
            frame.repaint();
        });

        logoutButton.addActionListener(e -> {
            SocialConnect.getWhoIsLoggedIn().logout();
            frame.remove(panel);
            createLoginPanel();
            frame.revalidate();
            frame.repaint();
        });

        frame.revalidate();
        frame.repaint();
    }

    /**
     * Displays all visible posts in the specified panel.
     * Posts are sorted according to the current sorting algorithm and filtered
     * based on
     * the viewing user's permissions (followers-only posts are only shown to
     * followers).
     *
     * @param panel  The panel where posts should be displayed
     * @param sortBy The algorithm to use for sorting posts
     */
    private void displayPosts(JPanel panel, PageAlgorithm sortBy) {
        panel.removeAll();
        SocialConnect.setSortBy(sortBy);
        List<Post> posts = SocialConnect.getVisiblePosts(SocialConnect.getWhoIsLoggedIn());

        if (posts.isEmpty()) {
            JLabel noPostsLabel = new JLabel("No posts to display");
            noPostsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(noPostsLabel);
        } else {
            for (Post post : posts) {
                panel.add(
                        createPostPanel(post, post.getOwner().equals(SocialConnect.getWhoIsLoggedIn().getUsername())));
                panel.add(Box.createVerticalStrut(10));
            }
        }

        panel.revalidate();
        panel.repaint();
    }

    /**
     * Creates a new panel for post creation with text input and audience selection.
     * Includes a text area for post content, audience selector dropdown, and
     * buttons
     * for post creation and navigation.
     */
    private void createPostPanel() {
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        frame.add(panel, BorderLayout.CENTER);

        JLabel titleLabel = new JLabel("Create New Post");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));

        JLabel postLabel = new JLabel("Post Text:");
        postLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextArea postText = new JTextArea(5, 20);
        postText.setLineWrap(true);
        postText.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(postText);
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel audiencePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel audienceLabel = new JLabel("Audience:");
        JComboBox<PostAudience> audienceComboBox = new JComboBox<>(PostAudience.values());
        audiencePanel.add(audienceLabel);
        audiencePanel.add(audienceComboBox);
        audiencePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton createPostButton = new JButton("Create Post");
        createPostButton.setBackground(new Color(0, 123, 255));
        createPostButton.setForeground(Color.WHITE);
        createPostButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(postLabel);
        panel.add(scrollPane);
        panel.add(Box.createVerticalStrut(10));
        panel.add(audiencePanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(createPostButton);
        panel.add(Box.createVerticalStrut(20));

        createPostButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = postText.getText().trim();
                if (text.isEmpty()) {
                    JOptionPane.showMessageDialog(panel, "Post text cannot be empty.");
                    return;
                }

                Post post = SocialConnect.getWhoIsLoggedIn().createPost(text,
                        (PostAudience) audienceComboBox.getSelectedItem());
                SocialConnect.logNewPost(post);
                JOptionPane.showMessageDialog(panel, "Post created successfully!");

                frame.getContentPane().removeAll();
                createDashboardPanel();
                frame.revalidate();
                frame.repaint();
            }
        });

        backButton = new JButton("Back to Dashboard");
        backButton.setBackground(new Color(108, 117, 125));
        backButton.setForeground(Color.WHITE);
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> {
            frame.getContentPane().removeAll();
            createDashboardPanel();
            frame.revalidate();
            frame.repaint();
        });
        panel.add(backButton);

        frame.revalidate();
        frame.repaint();
    }

    /**
     * Creates a panel displaying all posts created by the currently logged-in user.
     * Posts are displayed in a scrollable panel with options to delete posts.
     */
    private void createMyPostsPanel() {
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        frame.add(panel, BorderLayout.CENTER);

        JLabel titleLabel = new JLabel("My Posts");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));

        // Get user's posts
        List<Post> userPosts = SocialConnect.getUserPosts(SocialConnect.getWhoIsLoggedIn().getUsername());

        if (userPosts.isEmpty()) {
            JLabel noPostsLabel = new JLabel("You haven't created any posts yet!");
            noPostsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(noPostsLabel);
        } else {
            // Create a scrollable panel for posts
            JPanel postsPanel = new JPanel();
            postsPanel.setLayout(new BoxLayout(postsPanel, BoxLayout.Y_AXIS));

            for (Post post : userPosts) {
                JPanel postPanel = createPostPanel(post, true);
                postsPanel.add(postPanel);
                postsPanel.add(Box.createVerticalStrut(10));
            }

            JScrollPane scrollPane = new JScrollPane(postsPanel);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            panel.add(scrollPane);
        }

        // Back button
        backButton = new JButton("Back to Dashboard");
        backButton.setBackground(new Color(0, 123, 255));
        backButton.setForeground(Color.WHITE);
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> {
            frame.getContentPane().removeAll();
            createDashboardPanel();
            frame.revalidate();
            frame.repaint();
        });

        panel.add(Box.createVerticalStrut(20));
        panel.add(backButton);

        frame.revalidate();
        frame.repaint();
    }

    /**
     * Creates a panel for displaying a single post in the feed.
     * This method handles both regular text posts and image posts, including
     * voting buttons, reshare functionality, and delete options for owned posts.
     *
     * @param post             The Post object to display
     * @param showDeleteButton Whether to show the delete button (true for user's
     *                         own posts)
     * @return JPanel containing the formatted post with all interactive elements
     */
    private JPanel createPostPanel(Post post, boolean showDeleteButton) {
        JPanel postPanel = new JPanel();
        postPanel.setLayout(new BoxLayout(postPanel, BoxLayout.Y_AXIS));
        postPanel.setBackground(Color.WHITE);
        postPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        postPanel.setMaximumSize(new Dimension(600, Integer.MAX_VALUE));
        postPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Post header (username and audience)
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setMaximumSize(new Dimension(580, Integer.MAX_VALUE));
        JLabel usernameLabel = new JLabel("@" + post.getOwner());
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel audienceLabel = new JLabel(" • " + post.getAudience().toString());
        audienceLabel.setForeground(Color.GRAY);
        headerPanel.add(usernameLabel);
        headerPanel.add(audienceLabel);
        postPanel.add(headerPanel);

        // Post content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setMaximumSize(new Dimension(580, Integer.MAX_VALUE));
        contentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (post instanceof ImagePost) {
            ImagePost imagePost = (ImagePost) post;
            try {
                // Load and scale the image
                BufferedImage img = ImageIO.read(new File(imagePost.imagePath));
                if (img != null) {
                    int maxWidth = 550; // Increased max width
                    int maxHeight = 400; // Increased max height
                    int scaledWidth, scaledHeight;
                    double ratio = (double) img.getWidth() / img.getHeight();

                    if (ratio > 1) {
                        scaledWidth = maxWidth;
                        scaledHeight = (int) (maxWidth / ratio);
                    } else {
                        scaledHeight = maxHeight;
                        scaledWidth = (int) (maxHeight * ratio);
                    }

                    Image scaledImg = img.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                    JLabel imageLabel = new JLabel(new ImageIcon(scaledImg));
                    imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    contentPanel.add(Box.createVerticalStrut(10));
                    contentPanel.add(imageLabel);
                    contentPanel.add(Box.createVerticalStrut(10));
                }

                // Add caption
                JTextArea captionArea = new JTextArea(imagePost.caption);
                captionArea.setWrapStyleWord(true);
                captionArea.setLineWrap(true);
                captionArea.setEditable(false);
                captionArea.setBackground(Color.WHITE);
                captionArea.setMaximumSize(new Dimension(550, Integer.MAX_VALUE));
                captionArea.setAlignmentX(Component.LEFT_ALIGNMENT);
                contentPanel.add(captionArea);
            } catch (IOException e) {
                JLabel errorLabel = new JLabel("Error loading image");
                errorLabel.setForeground(Color.RED);
                contentPanel.add(errorLabel);
            }
        } else {
            JTextArea textArea = new JTextArea(post.getText());
            textArea.setWrapStyleWord(true);
            textArea.setLineWrap(true);
            textArea.setEditable(false);
            textArea.setBackground(Color.WHITE);
            textArea.setMaximumSize(new Dimension(550, Integer.MAX_VALUE));
            textArea.setAlignmentX(Component.LEFT_ALIGNMENT);
            contentPanel.add(textArea);
        }
        postPanel.add(contentPanel);

        // Voting and action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);

        // Only show voting buttons if it's not the user's own post
        if (!post.getOwner().equals(SocialConnect.getWhoIsLoggedIn().getUsername())) {
            // Upvote button
            JButton upvoteButton = new JButton("▲");
            upvoteButton.setFont(new Font("Arial", Font.PLAIN, 16));
            updateVoteButtonState(upvoteButton, post.hasVoted(SocialConnect.getWhoIsLoggedIn(), true), true);

            // Downvote button
            JButton downvoteButton = new JButton("▼");
            downvoteButton.setFont(new Font("Arial", Font.PLAIN, 16));
            updateVoteButtonState(downvoteButton, post.hasVoted(SocialConnect.getWhoIsLoggedIn(), false), false);

            // Vote count label
            JLabel voteCountLabel = new JLabel(String.valueOf(post.getVoteCount()));
            voteCountLabel.setFont(new Font("Arial", Font.BOLD, 14));
            voteCountLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

            buttonPanel.add(upvoteButton);
            buttonPanel.add(voteCountLabel);
            buttonPanel.add(downvoteButton);

            // Add voting functionality
            upvoteButton.addActionListener(e -> {
                post.vote(SocialConnect.getWhoIsLoggedIn(), true);
                voteCountLabel.setText(String.valueOf(post.getVoteCount()));
                updateVoteButtonState(upvoteButton, post.hasVoted(SocialConnect.getWhoIsLoggedIn(), true), true);
                updateVoteButtonState(downvoteButton, post.hasVoted(SocialConnect.getWhoIsLoggedIn(), false), false);
            });

            downvoteButton.addActionListener(e -> {
                post.vote(SocialConnect.getWhoIsLoggedIn(), false);
                voteCountLabel.setText(String.valueOf(post.getVoteCount()));
                updateVoteButtonState(upvoteButton, post.hasVoted(SocialConnect.getWhoIsLoggedIn(), true), true);
                updateVoteButtonState(downvoteButton, post.hasVoted(SocialConnect.getWhoIsLoggedIn(), false), false);
            });
        }

        // Reshare button
        JButton reshareButton = new JButton("Reshare");
        reshareButton.setBackground(new Color(40, 167, 69));
        reshareButton.setForeground(Color.WHITE);
        buttonPanel.add(reshareButton);

        // Delete button if it's the user's own post
        if (showDeleteButton) {
            JButton deleteButton = new JButton("Delete");
            deleteButton.setBackground(new Color(220, 53, 69));
            deleteButton.setForeground(Color.WHITE);
            buttonPanel.add(deleteButton);

            deleteButton.addActionListener(e -> {
                int choice = JOptionPane.showConfirmDialog(
                        frame,
                        "Are you sure you want to delete this post?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION);

                if (choice == JOptionPane.YES_OPTION) {
                    SocialConnect.deletePost(post);
                    frame.getContentPane().removeAll();
                    createDashboardPanel();
                    frame.revalidate();
                    frame.repaint();
                }
            });
        }

        postPanel.add(buttonPanel);

        // Add action listener for reshare button
        reshareButton.addActionListener(e -> createReshareDialog(post));

        return postPanel;
    }

    /**
     * Updates the visual state of a voting button based on the user's vote status.
     * Changes the button's background and foreground colors to indicate if the user
     * has voted in that direction.
     *
     * @param button   The button to update (either upvote or downvote)
     * @param isActive Whether the user has an active vote in this direction
     * @param isUpvote Whether this is an upvote button (true) or downvote button
     *                 (false)
     */
    private void updateVoteButtonState(JButton button, boolean isActive, boolean isUpvote) {
        if (isActive) {
            button.setBackground(isUpvote ? new Color(144, 238, 144) : new Color(255, 182, 193));
            button.setOpaque(true);
        } else {
            button.setBackground(Color.WHITE);
            button.setOpaque(false);
        }
    }

    /**
     * Creates a dialog for resharing an existing post.
     * This dialog allows users to add their own comment to the original post
     * and select the audience for the reshared post.
     *
     * @param originalPost The post to be reshared
     */
    private void createReshareDialog(Post originalPost) {
        // Save current panel to return to
        JPanel previousPanel = panel;

        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        frame.add(panel);

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("Reshare Post");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(titleLabel);
        panel.add(headerPanel, BorderLayout.NORTH);

        // Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.setBackground(Color.WHITE);

        // Original post display
        JLabel originalPostLabel = new JLabel("Original Post:");
        originalPostLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JTextArea originalPostArea = new JTextArea(originalPost.toString());
        originalPostArea.setEditable(false);
        originalPostArea.setLineWrap(true);
        originalPostArea.setWrapStyleWord(true);
        originalPostArea.setBackground(new Color(245, 245, 245));
        JScrollPane originalScrollPane = new JScrollPane(originalPostArea);
        originalScrollPane.setPreferredSize(new Dimension(500, 100));

        // Comment input
        JLabel commentLabel = new JLabel("Your Comment:");
        commentLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JTextArea commentArea = new JTextArea(3, 20);
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        JScrollPane commentScrollPane = new JScrollPane(commentArea);

        // Audience selection
        JLabel audienceLabel = new JLabel("Share with:");
        audienceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JComboBox<PostAudience> audienceCombo = new JComboBox<>();
        PostAudience[] audiences = PostAudience.values();
        for (PostAudience audience : audiences) {
            if (audience.ordinal() <= originalPost.getAudience().ordinal()) {
                audienceCombo.addItem(audience);
            }
        }

        // Add components to content panel
        contentPanel.add(originalPostLabel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(originalScrollPane);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(commentLabel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(commentScrollPane);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(audienceLabel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(audienceCombo);

        panel.add(contentPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton backButton = new JButton("Back");
        backButton.setBackground(new Color(108, 117, 125));
        backButton.setForeground(Color.WHITE);

        JButton shareButton = new JButton("Share");
        shareButton.setBackground(new Color(0, 123, 255));
        shareButton.setForeground(Color.WHITE);

        backButton.addActionListener(e -> {
            frame.remove(panel);
            frame.add(previousPanel);
            frame.revalidate();
            frame.repaint();
        });

        shareButton.addActionListener(e -> {
            try {
                ResharedPost resharedPost = new ResharedPost(
                        ((PostAudience) audienceCombo.getSelectedItem()).toString(),
                        originalPost.getPostID(),
                        originalPost,
                        SocialConnect.getWhoIsLoggedIn().getUsername());
                SocialConnect.logNewPost(resharedPost);
                frame.getContentPane().removeAll();
                createDashboardPanel(); // Return to dashboard after successful share
                frame.revalidate();
                frame.repaint();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(frame,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(backButton);
        buttonPanel.add(shareButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        frame.remove(previousPanel);
        frame.revalidate();
        frame.repaint();
    }

    private void createFollowManagement() {
        panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        frame.add(panel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Manage Connections");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // Search Section
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createTitledBorder("Find Users"));

        GridBagConstraints searchGbc = new GridBagConstraints();
        searchGbc.insets = new Insets(5, 5, 5, 5);
        searchGbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField searchField = new JTextField(20);
        searchGbc.gridx = 0;
        searchGbc.gridy = 0;
        searchGbc.weightx = 1.0;
        searchPanel.add(searchField, searchGbc);

        JButton searchButton = new JButton("Search");
        searchButton.setBackground(new Color(0, 123, 255));
        searchButton.setForeground(Color.WHITE);
        searchGbc.gridx = 1;
        searchGbc.gridy = 0;
        searchGbc.weightx = 0;
        searchPanel.add(searchButton, searchGbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        panel.add(searchPanel, gbc);

        // Results Panel
        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBackground(Color.WHITE);

        JScrollPane resultsScroll = new JScrollPane(resultsPanel);
        resultsScroll.setBorder(BorderFactory.createTitledBorder("Search Results"));
        resultsScroll.setPreferredSize(new Dimension(0, 150)); // Fixed height for results

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2; // Span across
        gbc.weighty = 0.3; // Give some vertical space
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(resultsScroll, gbc);

        // Lists Panel (Side by Side)
        JPanel listsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        listsPanel.setBackground(Color.WHITE);

        // Following Panel
        JPanel followingPanel = new JPanel();
        followingPanel.setLayout(new BoxLayout(followingPanel, BoxLayout.Y_AXIS));
        followingPanel.setBackground(Color.WHITE);
        JScrollPane followingScroll = new JScrollPane(followingPanel);
        followingScroll.setBorder(BorderFactory.createTitledBorder("Following"));
        listsPanel.add(followingScroll);

        // Followers Panel
        JPanel followersPanel = new JPanel();
        followersPanel.setLayout(new BoxLayout(followersPanel, BoxLayout.Y_AXIS));
        followersPanel.setBackground(Color.WHITE);
        JScrollPane followersScroll = new JScrollPane(followersPanel);
        followersScroll.setBorder(BorderFactory.createTitledBorder("Followers"));
        listsPanel.add(followersScroll);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weighty = 0.7; // Give more space to lists
        panel.add(listsPanel, gbc);

        // Back Button
        backButton = new JButton("Back to Dashboard");
        backButton.setBackground(new Color(108, 117, 125));
        backButton.setForeground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(backButton, gbc);

        updateFollowlists(followingPanel, followersPanel);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchTerm = searchField.getText().trim();
                if (searchTerm.isEmpty()) {
                    JOptionPane.showMessageDialog(panel, "Please enter a username to search for.");
                    return;
                }

                resultsPanel.removeAll();
                boolean foundUsers = false;

                for (User member : SocialConnect.getMembers()) {
                    if (member.getUsername().contains(searchTerm) &&
                            !member.getUsername().equals(SocialConnect.getWhoIsLoggedIn().getUsername())) {
                        foundUsers = true;
                        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                        userPanel.setBackground(Color.WHITE);
                        // Make sure items don't stretch vertically in BoxLayout
                        userPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

                        JLabel usernameLabel = new JLabel(member.getUsername() + " ");
                        JButton followButton = new JButton(
                                SocialConnect.getWhoIsLoggedIn().isFollowing(member.getUsername()) ? "Unfollow"
                                        : "Follow");

                        if (SocialConnect.getWhoIsLoggedIn().isFollowing(member.getUsername())) {
                            followButton.setBackground(new Color(220, 53, 69)); // Red for Unfollow
                        } else {
                            followButton.setBackground(new Color(40, 167, 69)); // Green for Follow
                        }
                        followButton.setForeground(Color.WHITE);

                        followButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                User currentUser = SocialConnect.getWhoIsLoggedIn();
                                String targetUsername = member.getUsername();

                                if (followButton.getText().equals("Follow")) {
                                    try {
                                        currentUser.startFollowing(targetUsername);
                                        member.addFollower(currentUser.getUsername());
                                        followButton.setText("Unfollow");
                                        followButton.setBackground(new Color(220, 53, 69));
                                    } catch (IllegalArgumentException ex) {
                                        JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error",
                                                JOptionPane.ERROR_MESSAGE);
                                        return;
                                    }
                                } else {
                                    try {
                                        currentUser.stopFollowing(targetUsername);
                                        member.removeFollower(currentUser.getUsername());
                                        followButton.setText("Follow");
                                        followButton.setBackground(new Color(40, 167, 69));
                                    } catch (IllegalArgumentException ex) {
                                        JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error",
                                                JOptionPane.ERROR_MESSAGE);
                                        return;
                                    }
                                }
                                updateFollowlists(followingPanel, followersPanel);
                            }
                        });

                        userPanel.add(usernameLabel);
                        userPanel.add(followButton);
                        resultsPanel.add(userPanel);
                    }
                }

                if (!foundUsers) {
                    JPanel noResultsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                    noResultsPanel.setBackground(Color.WHITE);
                    noResultsPanel.add(new JLabel("No users found matching: " + searchTerm));
                    resultsPanel.add(noResultsPanel);
                }

                resultsPanel.revalidate();
                resultsPanel.repaint();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.getContentPane().removeAll();
                createDashboardPanel();
                frame.revalidate();
                frame.repaint();
            }
        });
    }

    private void updateFollowlists(JPanel followingPanel, JPanel followersPanel) {
        User currentUser = SocialConnect.getWhoIsLoggedIn();
        followingPanel.removeAll();
        followersPanel.removeAll();

        // Following list (people I follow)
        ArrayList<String> following = currentUser.getFollowing();
        if (following.isEmpty()) {
            JLabel noFollowingLabel = new JLabel("Not following anyone");
            noFollowingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            followingPanel.add(noFollowingLabel);
        } else {
            for (String username : following) {
                JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                userPanel.setBackground(Color.WHITE);
                userPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

                JLabel usernameLabel = new JLabel(username);
                JButton unfollowButton = new JButton("Unfollow");
                unfollowButton.setBackground(new Color(220, 53, 69));
                unfollowButton.setForeground(Color.WHITE);

                unfollowButton.addActionListener(e -> {
                    try {
                        currentUser.stopFollowing(username);
                        // Find and update the user being unfollowed
                        for (User member : SocialConnect.getMembers()) {
                            if (member.getUsername().equals(username)) {
                                member.removeFollower(currentUser.getUsername());
                                break;
                            }
                        }
                        updateFollowlists(followingPanel, followersPanel);
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });

                userPanel.add(usernameLabel);
                userPanel.add(unfollowButton);
                followingPanel.add(userPanel);
            }
        }

        // Followers list (people who follow me)
        ArrayList<String> followers = currentUser.getFollowers();
        if (followers.isEmpty()) {
            JLabel noFollowersLabel = new JLabel("No followers yet");
            noFollowersLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            followersPanel.add(noFollowersLabel);
        } else {
            for (String username : followers) {
                JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                userPanel.setBackground(Color.WHITE);
                userPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

                JLabel usernameLabel = new JLabel(username);

                // Only show Follow Back button if we're not already following them
                if (!currentUser.isFollowing(username)) {
                    JButton followBackButton = new JButton("Follow Back");
                    followBackButton.setBackground(new Color(40, 167, 69));
                    followBackButton.setForeground(Color.WHITE);

                    followBackButton.addActionListener(e -> {
                        try {
                            currentUser.startFollowing(username);
                            // Find and update the user being followed
                            for (User member : SocialConnect.getMembers()) {
                                if (member.getUsername().equals(username)) {
                                    member.addFollower(currentUser.getUsername());
                                    break;
                                }
                            }
                            updateFollowlists(followingPanel, followersPanel);
                        } catch (IllegalArgumentException ex) {
                            JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    });

                    userPanel.add(usernameLabel);
                    userPanel.add(followBackButton);
                } else {
                    userPanel.add(usernameLabel);
                    JLabel followingLabel = new JLabel("(Following)");
                    followingLabel.setForeground(new Color(40, 167, 69));
                    userPanel.add(followingLabel);
                }
                followersPanel.add(userPanel);
            }
        }

        followingPanel.revalidate();
        followingPanel.repaint();
        followersPanel.revalidate();
        followersPanel.repaint();
    }

    private void displayPosts(JPanel panel) {
        panel.removeAll();
        for (Post post : SocialConnect.getPosts()) {
            JPanel postPanel = createPostPanel(post, false);
            panel.add(postPanel);
        }
        panel.revalidate();
        panel.repaint();
    }

    public void postMade() {
        if (!panel.isAncestorOf(backButton)) {
            panel.add(backButton);
        }
        panel.revalidate();
        panel.repaint();
    }

    public void refreshPosts(JPanel targetPanel) {
        targetPanel.removeAll();
        displayPosts(targetPanel);
        if (!targetPanel.isAncestorOf(backButton)) {
            targetPanel.add(backButton);
        }
        targetPanel.revalidate();
        targetPanel.repaint();
    }

    private void createImagePostPanel() {
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        JPanel imagePostPanel = new JPanel();
        imagePostPanel.setLayout(new BoxLayout(imagePostPanel, BoxLayout.Y_AXIS));
        imagePostPanel.setBackground(Color.WHITE);

        // Caption input
        JLabel captionLabel = new JLabel("Caption:");
        captionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextArea captionText = new JTextArea(3, 20);
        captionText.setLineWrap(true);
        captionText.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(captionText);
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Image selection
        JButton chooseImageButton = new JButton("Choose Image");
        chooseImageButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel imagePathLabel = new JLabel("No image selected");
        imagePathLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Image preview panel
        JPanel previewPanel = new JPanel();
        previewPanel.setLayout(new BoxLayout(previewPanel, BoxLayout.Y_AXIS));
        previewPanel.setBackground(Color.WHITE);
        previewPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel previewLabel = new JLabel();
        previewLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        previewPanel.add(previewLabel);

        // Audience selection
        JPanel audiencePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        audiencePanel.setBackground(Color.WHITE);
        JLabel audienceLabel = new JLabel("Audience:");
        JComboBox<PostAudience> audienceComboBox = new JComboBox<>(PostAudience.values());
        audiencePanel.add(audienceLabel);
        audiencePanel.add(audienceComboBox);

        // Create post button
        JButton createPostButton = new JButton("Post Image");
        createPostButton.setBackground(new Color(0, 123, 255));
        createPostButton.setForeground(Color.WHITE);
        createPostButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        createPostButton.setEnabled(false); // Disabled until image is selected

        final String[] selectedImagePath = { null };

        chooseImageButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                public boolean accept(File f) {
                    if (f.isDirectory())
                        return true;
                    String name = f.getName().toLowerCase();
                    return name.endsWith(".jpg") || name.endsWith(".jpeg") ||
                            name.endsWith(".png") || name.endsWith(".gif");
                }

                public String getDescription() {
                    return "Image files (*.jpg, *.jpeg, *.png, *.gif)";
                }
            });

            int result = fileChooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                selectedImagePath[0] = selectedFile.getAbsolutePath();
                imagePathLabel.setText(selectedFile.getName());
                createPostButton.setEnabled(true);

                try {
                    // Load and scale image for preview
                    BufferedImage img = ImageIO.read(selectedFile);
                    if (img != null) {
                        int maxWidth = 300;
                        int maxHeight = 300;
                        int scaledWidth, scaledHeight;
                        double ratio = (double) img.getWidth() / img.getHeight();

                        if (ratio > 1) {
                            scaledWidth = maxWidth;
                            scaledHeight = (int) (maxWidth / ratio);
                        } else {
                            scaledHeight = maxHeight;
                            scaledWidth = (int) (maxHeight * ratio);
                        }

                        Image scaledImg = img.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                        previewLabel.setIcon(new ImageIcon(scaledImg));
                        previewPanel.revalidate();
                        previewPanel.repaint();
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame,
                            "Error loading image: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    selectedImagePath[0] = null;
                    createPostButton.setEnabled(false);
                    previewLabel.setIcon(null);
                }
            }
        });

        createPostButton.addActionListener(e -> {
            String caption = captionText.getText().trim();
            if (caption.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a caption for your image.");
                return;
            }

            if (selectedImagePath[0] == null) {
                JOptionPane.showMessageDialog(frame, "Please select an image first.");
                return;
            }

            try {
                SocialConnect.getWhoIsLoggedIn().createImagePost(
                        captionText.getText(),
                        selectedImagePath[0],
                        (PostAudience) audienceComboBox.getSelectedItem());
                frame.getContentPane().removeAll();
                createDashboardPanel();
                frame.revalidate();
                frame.repaint();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame,
                        "Error creating post: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // Add components to panel
        imagePostPanel.add(Box.createVerticalStrut(10));
        imagePostPanel.add(captionLabel);
        imagePostPanel.add(Box.createVerticalStrut(5));
        imagePostPanel.add(scrollPane);
        imagePostPanel.add(Box.createVerticalStrut(10));
        imagePostPanel.add(chooseImageButton);
        imagePostPanel.add(Box.createVerticalStrut(5));
        imagePostPanel.add(imagePathLabel);
        imagePostPanel.add(Box.createVerticalStrut(10));
        imagePostPanel.add(previewPanel);
        imagePostPanel.add(Box.createVerticalStrut(10));
        imagePostPanel.add(audiencePanel);
        imagePostPanel.add(Box.createVerticalStrut(10));
        imagePostPanel.add(createPostButton);
        imagePostPanel.add(Box.createVerticalStrut(20));

        // Top panel with back button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.WHITE);
        JButton backButton = new JButton("Back to Dashboard");
        backButton.setBackground(new Color(108, 117, 125));
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(e -> {
            frame.getContentPane().removeAll();
            createDashboardPanel();
            frame.revalidate();
            frame.repaint();
        });
        topPanel.add(backButton);

        // Add title
        JLabel titleLabel = new JLabel("Create Image Post");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        // Add components to main panel
        panel.setLayout(new BorderLayout());
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(titleLabel, BorderLayout.CENTER);
        panel.add(imagePostPanel, BorderLayout.SOUTH);

        // Make sure the panel is visible in the frame
        frame.getContentPane().removeAll();
        frame.getContentPane().add(panel);
        frame.revalidate();
        frame.repaint();
    }

    private void showImagePostPanel() {
        createImagePostPanel();
    }

    private void refreshDashboard() {
        frame.getContentPane().removeAll();
        createDashboardPanel();
        frame.revalidate();
        frame.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                String socialsName = "My SocialConnect";
                if (socialsName.length() < 8) {
                    throw new IllegalArgumentException("Socials name must be at least 8 characters long");
                }
                SocialConnect SocialConnect = new SocialConnect(socialsName, PageAlgorithm.Newest);
                new SocialConnectGUI(SocialConnect);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Error starting application: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
