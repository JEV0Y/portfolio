package model.enumerations;

public enum Action {
	Add("Add member to group"), Leave("Leave group"), Remove("Remove member from group"),
	Upgrade("Upgrad regular member to admin"), Downgrade("Downgrade admin to regular member"),
	Deactivate("Deactivated group"), Post("Make Post"), Reply("Make reply to post");

	String description;

	Action(String s) {
		description = s;
	}

	public String getDescription() {
		return description;
	}
}
