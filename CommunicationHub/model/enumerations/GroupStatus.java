package model.enumerations;

public enum GroupStatus {
	Active("Active"), Deactivated("Deactivated");

	String description;

	GroupStatus(String s) {
		description = s;
	}

	public String getDescription() {
		return description;
	}
}

