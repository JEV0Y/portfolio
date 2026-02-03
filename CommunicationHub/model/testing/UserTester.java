package model.testing;

import model.User;
import model.enumerations.GroupType;
import model.groups.Group;

public class UserTester {

	public static void main(String[] args) {

		User r = new User("Phil", "Jackson", "879-111-0000"), s = new User("Jenn", "Phipps", "876-121-2222"),
				t = new User("Amari", "Apple", "876-131-0010");

		System.out.println("Created User r: " + r);
		System.out.print("\tContact [" + s + "]" + (r.addContact(s) ? " added successfully" : " was not added"));
		System.out.println(" to User r.");
		System.out.print("\tContact [" + t + "]" + (r.addContact(t) ? " added successfully" : " was not added"));
		System.out.println(" to User r.");
		System.out.println();
		System.out.println("User r: \n" + r.getFullInformation());
	}
}

