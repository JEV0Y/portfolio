package model.enumerations;

public

enum GroupType {
	UsertoUser("Private"), MessagesToSelf("Messages to Self"), RegularGroup("Regular");

	String description;

	GroupType(String s) {
		description = s;
	}

	public String getDescription() {
		return description;
	}

}

