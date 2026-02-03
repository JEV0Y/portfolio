package project;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import project.enums.PostAudience;

/**
 * Represents an image post in the social media platform.
 * This class extends the base Post class and adds functionality specific to image posts.
 */
public class ImagePost extends Post {
    /** The caption for this image post */
    public String caption;
    
    /** The loaded image */
    public BufferedImage image;
    
    /** The path to the image file */
    public String imagePath;

    /**
     * Creates a new image post.
     * 
     * @param whoCanSee The audience for this post
     * @param owner The username of the post owner
     * @param imagePath The path to the image file
     * @param caption The caption for the image
     * @throws IOException If there is an error loading the image
     */
    public ImagePost(PostAudience whoCanSee, String owner, String imagePath, String caption) throws IOException {
        super(whoCanSee, owner);
        this.caption = caption;
        this.imagePath = imagePath;
        loadImage();
    }

    /**
     * Loads the image from the file system.
     * 
     * @throws IOException If there is an error loading the image
     */
    private void loadImage() throws IOException {
        File imageFile = new File(imagePath);
        if (!imageFile.exists()) {
            throw new IOException("Image file not found: " + imagePath);
        }
        this.image = ImageIO.read(imageFile);
    }

    @Override
    public String getText() {
        return caption;
    }

    @Override
    public JPanel display() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add image
        if (image != null) {
            // Create a wrapper panel that will handle the image resizing
            JPanel imageWrapper = new JPanel(new BorderLayout());
            imageWrapper.setBackground(Color.WHITE);
            
            // Create a custom JLabel that will resize the image based on available width
            JLabel imageLabel = new JLabel() {
                @Override
                public void setBounds(int x, int y, int width, int height) {
                    super.setBounds(x, y, width, height);
                    if (width > 0 && image != null) {
                        // Calculate scaled dimensions while maintaining aspect ratio
                        double scale = (double) width / image.getWidth();
                        int scaledHeight = (int) (image.getHeight() * scale);
                        
                        // Limit maximum height if needed
                        int maxHeight = 600;
                        if (scaledHeight > maxHeight) {
                            scale = (double) maxHeight / image.getHeight();
                            width = (int) (image.getWidth() * scale);
                            scaledHeight = maxHeight;
                        }
                        
                        // Create scaled version of the image
                        Image scaledImage = image.getScaledInstance(width, scaledHeight, Image.SCALE_SMOOTH);
                        setIcon(new ImageIcon(scaledImage));
                    }
                }
            };
            
            imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            imageWrapper.add(imageLabel, BorderLayout.CENTER);
            panel.add(imageWrapper);
            
            // Add a component listener to handle resize events
            panel.addComponentListener(new java.awt.event.ComponentAdapter() {
                @Override
                public void componentResized(java.awt.event.ComponentEvent e) {
                    int availableWidth = panel.getWidth() - 20; // Account for border
                    if (availableWidth > 0) {
                        imageLabel.setBounds(0, 0, availableWidth, 0);
                        panel.revalidate();
                    }
                }
            });
        }

        // Add caption if present
        if (caption != null && !caption.isEmpty()) {
            JLabel captionLabel = new JLabel("<html><body style='width: 100%'>" + caption + "</body></html>");
            captionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(Box.createVerticalStrut(10));
            panel.add(captionLabel);
        }

        return panel;
    }

    @Override
    public String toString() {
        return String.format("[Image Post #%d]\nBy: %s\nCaption: %s\nImage: %s",
            getPostID(),
            getOwner(),
            caption,
            imagePath);
    }
}
