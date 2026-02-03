package project.interfaces;

import javax.swing.JPanel;

/**
 * @author 
 * 
 *
 * Interface for objects that can be displayed in the SocialConnect application's GUI.
 * Classes that implement this interface must provide a method to create a visual
 * representation of themselves using Swing components.
 * 
 * This interface is used by various components in the application to ensure
 * consistent display behavior, particularly for posts and user content.
 */
public interface Displayable {
    /**
     * Creates and returns a JPanel containing a visual representation of the object.
     * Implementing classes should create an appropriate layout and add necessary
     * Swing components to display their content.
     *
     * @return A JPanel containing the visual representation of the object
     */
    public JPanel display();
}
