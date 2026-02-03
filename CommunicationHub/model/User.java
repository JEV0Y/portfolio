package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeSet;
import model.groups.Group;
import model.interfaces.FullInformation;
import model.interfaces.Verify;
import model.enumerations.Action;

public class User implements Comparable<User>, FullInformation, Verify {
	/*
	 * The natural ordering is by phone number using the Comparable interface. This
	 * Comparator is defined so we can order Users by full name as would be
	 * required when a User vies their contacts.
	 */
	class SameName implements Comparator<User> {

		@Override
		public int compare(User o1, User o2) {
			return o1.getFullName().compareTo(o2.getFullName());
		}
	}

	private String firstname, lastname;
	private final PhoneNumber phoneNumber;
	private TreeSet<User> contacts;
	private TreeSet<Group> memberOfGroups;
	private TreeSet<Group> noLongerMemberOfGroups;

	public User(String firstname, String lastname, String phoneNumber) {
		this.firstname = (firstname == null ? "" : firstname);
		this.lastname = (lastname == null ? "" : lastname);

		if (phoneNumber == null || !PhoneNumber.isValid(phoneNumber))
			this.phoneNumber = new PhoneNumber("000", "000", "0000");
		else {
			String p[] = phoneNumber.split("-");
			this.phoneNumber = new PhoneNumber(p[0], p[1], p[2]);
		}

		contacts = new TreeSet<User>();
		memberOfGroups = new TreeSet<Group>();
		noLongerMemberOfGroups = new TreeSet<Group>();
	}

	public PhoneNumber getPhoneNumber() {
		/*
		 * Like Strings, since the internals of a phone number cannot be changed, it is
		 * OK to return a pointer (that is an alias) to it.
		 */
		return phoneNumber;
	}

	public TreeSet<Group> getGroups() {
		return new TreeSet<Group>(memberOfGroups);
	}

	public String getFirstname() {
		return firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public String getFullName() {
		return lastname + ",  " + firstname;
	}

	public boolean setFirstname(String name) {
		firstname = name != null ? name.equals(firstname) ? firstname : name : firstname;
		return firstname == name;
	}

	public boolean setLastname(String name) {
		lastname = name != null ? name.equals(lastname) ? lastname : name : lastname;
		return lastname == name;
	}

	/* TO DO */
	public boolean isAContact(String name) {
		if (name == null)
			return false;
		for (User contact : contacts) {
			if (contact.getFullName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	/* TO DO */
	public boolean isAContact(PhoneNumber phoneNumber) {
		if (phoneNumber == null)
			return false;
		for (User contact : contacts) {
			if (contact.getPhoneNumber().compareTo(phoneNumber) == 0) {
				return true;
			}
		}
		return false;
	}

	/* TO DO */
	public boolean isAContact(User User) {
		if (User == null)
			return false;
		return contacts.contains(User);
	}

	/* TO DO */
	public boolean isMyNumber(String phoneNumber) {
		if (phoneNumber == null || !PhoneNumber.isValid(phoneNumber)) {
			return false;
		}
		String p[] = phoneNumber.split("-");
		PhoneNumber parsedNumber = new PhoneNumber(p[0], p[1], p[2]);
		return this.phoneNumber.compareTo(parsedNumber) == 0;
	}

	/* TO DO */
	public boolean isMyNumber(PhoneNumber phoneNumber) {
		if (phoneNumber == null) {
			return false;
		}
		return this.phoneNumber.compareTo(phoneNumber) == 0;
	}

	/* TO DO */
	public boolean isMemberOfGroup(Group group) {
		if (group == null) {
			return false;
		}
		return memberOfGroups.contains(group);
	}

	/* TO DO */
	public boolean isCreatorOfGroup(Group group) {
		if (group == null) {
			return false;
		}
		// Check if this User is a member of the group
		if (!isMemberOfGroup(group)) {
			return false;
		}
		// Check if this User is an admin (creator is always an admin)
		// Note: This is a workaround since we can't access protected creator field
		// The creator is always the first admin, but we can't verify that without
		// accessing protected members. This checks if User is admin and member.
		return group.isAdmin(this);
	}

	public boolean addContact(User User) {
		return contacts.add(User);
	}

	public boolean addGroupMembership(Group g) {
		return memberOfGroups.add(g);
	}

	/* TO DO */
	public boolean removeGroupMembership(Group g) {
		/*
		 * membership cannot be removed if the this User is the creator of the group
		 */
		if (g == null) {
			return false;
		}
		// Cannot remove membership if this User is the creator
		if (isCreatorOfGroup(g)) {
			return false;
		}
		// Remove from current memberships
		boolean removed = memberOfGroups.remove(g);
		if (removed) {
			// Add to past memberships
			noLongerMemberOfGroups.add(g);
		}
		return removed;
	}

	@Override
	public int compareTo(User o) {
		return phoneNumber.compareTo(o.getPhoneNumber());
	}

	@Override /* returns basic information for a User */
	public String toString() {
		return firstname + " " + lastname + ", " + phoneNumber.toString();
	}

	private String contactsToString() {
		String str = "";
		ArrayList<User> tW = new ArrayList<User>();
		for (User w : contacts)
			tW.add(w);
		Collections.sort(tW, new SameName());
		for (User w : tW)
			str += "\t" + w.toString() + (w == tW.getLast() ? "" : "\n");
		return str;
	}

	private String groupMembershipsToString() {
		String str = "";
		for (Group g : memberOfGroups)
			str += "\t" + g.toString() + " " + (g.isAdmin(this) ? "(is admin)" : "")
					+ (g == memberOfGroups.last() ? "" : "\n");
		return str;
	}

	/*
	 * This provided so that you can pass the action and its associated parameters
	 * to the method.
	 * 
	 * The method must parse the parameters parameter and you may assume that the
	 * order of vales in parameters are in the order that the method that will be
	 * called from the group class should take.
	 * 
	 * For example to create a group, the objects in the parameters should be passed
	 * those required for and in the order required in the Group constructor.
	 * 
	 * You should also notice that the addGroupMembership is called in the addMember
	 * and in the constructor methods in the Group class - this ensures
	 * well-formedness constraints are not violated.
	 * 
	 * This same discussion should be applied to the calling of the
	 * removeGroupMembership method in this class: it should only be called from the
	 * Leave or Remove actions in the group class.
	 * 
	 * The other public methods in this class do not need to be called through the
	 * doGroupAction method, but may be called directly.
	 * 
	 * Your Javadoc documentation must outline the order of the elements and their
	 * types in the parameters parameter for each method that can be called.
	 */
	public boolean doGroupAction(Action action, Object... parameters) {
		if (action == null || parameters == null) {
			return false;
		}

		try {
			switch (action) {
				case Add:
					// Parameters: (Group group, User newMember)
					if (parameters.length != 2 || !(parameters[0] instanceof Group)
							|| !(parameters[1] instanceof User)) {
						return false;
					}
					Group groupAdd = (Group) parameters[0];
					User newMember = (User) parameters[1];
					return groupAdd.addMember(this, newMember);

				case Leave:
					// Parameters: (Group group)
					if (parameters.length != 1 || !(parameters[0] instanceof Group)) {
						return false;
					}
					Group groupLeave = (Group) parameters[0];
					return groupLeave.leaveGroup(this);

				case Remove:
					// Parameters: (Group group, User potential)
					if (parameters.length != 2 || !(parameters[0] instanceof Group)
							|| !(parameters[1] instanceof User)) {
						return false;
					}
					Group groupRemove = (Group) parameters[0];
					User potential = (User) parameters[1];
					return groupRemove.removeMember(this, potential);

				default:
					// Other actions (Upgrade, Downgrade, Deactivate, Post, Reply)
					// should be called directly on Group, not through doGroupAction
					return false;
			}
		} catch (ClassCastException | ArrayIndexOutOfBoundsException e) {
			return false;
		}
	}

	@Override
	public String getFullInformation() {
		String str = firstname + " " + lastname + ", " + phoneNumber.getFullInformation();
		str += "\nContacts: " + (contacts.size() == 0 ? "none" : (contacts.size() + "\n" + contactsToString()));
		str += "\nMember of groups: "
				+ (memberOfGroups.size() == 0 ? "none" : (memberOfGroups.size() + "\n" + groupMembershipsToString()));
		return str + "\n";
	}

	@Override /* TO DO, incomplete, see below */
	public boolean verify() {
		/* memberOfGroups and noLongerMemberOfGroups do not overlap */
		boolean noOverlap = true;
		for (Group g : memberOfGroups)
			if (noLongerMemberOfGroups.contains(g))
				noOverlap = false;

		/* TO DO, is member of all groups in memberOfGroups */
		boolean hasMembershipinAll = true;
		for (Group g : memberOfGroups) {
			if (!g.isCurrentMember(this, this)) {
				hasMembershipinAll = false;
				break;
			}
		}

		/* TO DO, is not a member of any of the groups in noLongerMemberOfGroups */
		boolean noMembershipInAny = true;
		for (Group g : noLongerMemberOfGroups) {
			if (g.isCurrentMember(this, this)) {
				noMembershipInAny = false;
				break;
			}
		}

		return noOverlap && hasMembershipinAll && noMembershipInAny;
	}
}

