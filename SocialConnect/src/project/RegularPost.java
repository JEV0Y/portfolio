package project;

import project.enums.PostAudience;
import project.enums.PostDataType;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 
 * 
 */

/**
 * Represents a regular post in the SocialConnect application.
 * A regular post can contain text and image content created by a user.
 * This class extends the base Post class and implements its specific behavior.
 */
public class RegularPost extends Post {
    private List<Content> contents;

    /**
     * Constructs a new regular post with text only.
     *
     * @param owner The username of the post creator
     * @param text The text content of the post
     * @param whoCanSee The visibility setting for the post
     * @throws IllegalArgumentException if text is null or empty
     */
    public RegularPost(String owner, String text, PostAudience whoCanSee) {
        super(whoCanSee, owner);
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Post text cannot be null or empty");
        }
        this.contents = new ArrayList<>();
        this.contents.add(new Content(text));
    }

    /**
     * Constructs a new regular post with multiple content items (text and/or images).
     *
     * @param owner The username of the post creator
     * @param contents List of content items
     * @param whoCanSee The visibility setting for the post
     * @throws IllegalArgumentException if contents is null or empty
     */
    public RegularPost(String owner, List<Content> contents, PostAudience whoCanSee) {
        super(whoCanSee, owner);
        if (contents == null || contents.isEmpty()) {
            throw new IllegalArgumentException("Post contents cannot be null or empty");
        }
        this.contents = new ArrayList<>(contents);
    }

    /**
     * Returns the text content of the post.
     *
     * @return The post's text content
     */
    public String getText() {
        for (Content content : contents) {
            if (content.getTypeOfData() == PostDataType.Text) {
                return content.getData();
            }
        }
        return "";
    }

    /**
     * Returns all content items in this post.
     *
     * @return List of content items
     */
    public List<Content> getContents() {
        return new ArrayList<>(contents);
    }

    /**
     * Returns a string representation of the regular post.
     * The format includes post ID, owner, and content descriptions.
     *
     * @return A formatted string containing the post's details
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("[Regular Post #%d]\nBy: %s\n", getPostID(), getOwner()));
        
        for (Content content : contents) {
            if (content.getTypeOfData() == PostDataType.Text) {
                sb.append(content.getData()).append("\n");
            } else if (content.getTypeOfData() == PostDataType.Image) {
                sb.append("[Image: ").append(content.getData()).append("]\n");
            }
        }
        
        return sb.toString();
    }

    
    /** 
     * @return JPanel
     */
    @Override
    public JPanel display() {
        JPanel panel = super.display();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        // Add content display
        for (Content content : contents) {
            if (content.getTypeOfData() == PostDataType.Text) {
                JLabel textLabel = new JLabel("<html><body style='width: 300px'>" + 
                    content.getData().replace("\n", "<br>") + "</html>");
                textLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                panel.add(textLabel);
                panel.add(Box.createVerticalStrut(5));
            } else if (content.getTypeOfData() == PostDataType.Image) {
                try {
                    ImageIcon imageIcon = new ImageIcon(content.getData());
                    // Scale image if too large
                    if (imageIcon.getIconWidth() > 300) {
                        Image image = imageIcon.getImage();
                        Image scaledImage = image.getScaledInstance(300, -1, Image.SCALE_SMOOTH);
                        imageIcon = new ImageIcon(scaledImage);
                    }
                    JLabel imageLabel = new JLabel(imageIcon);
                    imageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    panel.add(imageLabel);
                    panel.add(Box.createVerticalStrut(5));
                } catch (Exception e) {
                    JLabel errorLabel = new JLabel("Error loading image: " + content.getData());
                    errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    panel.add(errorLabel);
                    panel.add(Box.createVerticalStrut(5));
                }
            }
        }
        
        return panel;
    }
}
