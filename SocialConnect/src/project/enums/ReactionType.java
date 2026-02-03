package project.enums;

/**
 * @author 
 * 
 *
 * Enumeration defining the types of reactions users can have to posts.
 * Each reaction affects the post's popularity score differently.
 */
public enum ReactionType {

	/** A positive reaction that increases a post's popularity score */
	Upvote("Really Like"), 

	/** A negative reaction that decreases a post's popularity score */
	Downvote("Really Dislike");

	private String description;

	/**
	 * Creates a new ReactionType value with the specified description.
	 * 
	 * @param description A human-readable description of the reaction
	 */
	ReactionType(String description) {
		this.description = description;
	}

	/**
	 * Gets the human-readable description of this reaction type.
	 * 
	 * @return The description string for this ReactionType value
	 */
	public String getDescription() {
		return description;
	}

}
