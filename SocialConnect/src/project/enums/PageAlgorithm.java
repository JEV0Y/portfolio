package project.enums;

/**
 * @author 
 * 
 *
 * Enumeration defining the different algorithms available for sorting posts on a user's page.
 * Each algorithm provides a different way to order posts based on specific criteria.
 */
public enum PageAlgorithm {

    /** Orders posts by their popularity score (upvotes minus downvotes), highest first */
    Popular("Popularity score, highest at the top"), 
    
    /** Orders posts chronologically with newest posts first */
    Newest("Newest first!"), 
    
    /** Orders posts chronologically with oldest posts first */
    Oldest("Oldest first!");

    private String description;

    /**
     * Creates a new PageAlgorithm value with the specified description.
     * 
     * @param description A human-readable description of the sorting algorithm
     */
    PageAlgorithm(String description) {
        this.description = description;
    }

    /**
     * Gets the human-readable description of this sorting algorithm.
     * 
     * @return The description string for this PageAlgorithm value
     */
    public String getDescription() {
        return description;
    }

}
