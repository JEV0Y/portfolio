package project.enums;

/**
 * @author 
 * 
 */
public enum PostDataType {
	Text("Text"), Image("Image");

	private String description;

	PostDataType(String description) {
		this.description = description;
	}

	/**
	 * Returns the description of the PostDataType.
	 * 
	 * @return The description of the PostDataType.
	 */
	public String getDescription() {
		return description;
	}

}
