package project;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import javax.swing.BoxLayout;

import project.enums.PostAudience;

/**
 * @author 
 * 
 */
/**
 * Represents a reshared post in the SocialConnect application.
 * A reshared post contains a reference to an original post along with an optional comment.
 * This class extends the base Post class and implements specific behavior for reshared content.
 */
public class ResharedPost extends Post {

    /** The audience setting for this post */
    private String audience;
    
    /** The ID of the original post */
    private int originalPostID;
    
    /** The original post being reshared */
    private Post originalPost;

    /**
     * Creates a new ResharedPost.
     * 
     * @param audience The audience setting for this post
     * @param originalPostID The ID of the original post
     * @param originalPost The original post being reshared
     * @param owner The username of the person resharing
     */
    public ResharedPost(String audience, int originalPostID, Post originalPost, String owner) {
        super(PostAudience.valueOf(audience), owner);
        this.audience = audience;
        this.originalPostID = originalPostID;
        this.originalPost = originalPost;
    }

    /**
     * Returns the text of the original post.
     *
     * @return The text of the original post
     */
    @Override
    public String getText() {
        return originalPost.getText();
    }

    /**
     * Creates a GUI panel to display the reshared post.
     * Includes both the reshare information and the original post content.
     *
     * @return A JPanel containing the reshared post's details
     */
    @Override
    public JPanel display() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        
        JLabel reshareLabel = new JLabel("Reshared from post ID: " + originalPostID);
        reshareLabel.setForeground(Color.GRAY);
        panel.add(reshareLabel);
        
        panel.add(originalPost.display());
        
        return panel;
    }

    /**
     * Returns a string representation of the reshared post.
     * The format includes the reshare information and the original post details.
     *
     * @return A formatted string containing the reshared post's details
     */
    @Override
    public String toString() {
        return "Reshared Post [ID=" + getPostID() + ", Original Post ID=" + originalPostID + 
               ", Owner=" + getOwner() + ", Audience=" + audience + "]";
    }
}
