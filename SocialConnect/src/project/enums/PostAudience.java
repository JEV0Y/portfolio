package project.enums;

/**
 * @author
 * 
 *
 *         Enumeration defining who can see a post in the SocialConnect
 *         application.
 *         Each value represents a different visibility level for posts.
 */
public enum PostAudience {
    /** Posts are only visible to users who follow the post creator */
    Followers("Only Followers"),

    /** Posts are visible to all users in the SocialConnect application */
    /** Posts are visible to all users in the SocialConnect application */
    Members("Anyone in the current SocialConnect");

    private String description;

    /**
     * Creates a new PostAudience value with the specified description.
     * 
     * @param description A human-readable description of the audience level
     */
    PostAudience(String description) {
        this.description = description;
    }

    /**
     * Gets the human-readable description of this audience level.
     * 
     * @return The description string for this PostAudience value
     */
    public String getDescription() {
        return description;
    }
}
