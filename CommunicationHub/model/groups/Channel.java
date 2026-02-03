package model.groups;

import java.util.Set;

import model.User;
import model.enumerations.GroupType;

public class Channel extends Group {
	private final Community community;

	public Channel(Community community, User creator, String name, Set<User> members) {
		super(creator, GroupType.RegularGroup, name, Group.MAX_GROUP_CAPACITY, members);
		this.community = community;
	}

	public Channel(Community community, User creator, String name, Set<User> members, long id) {
		super(creator, GroupType.RegularGroup, name, Group.MAX_GROUP_CAPACITY, members, id);
		this.community = community;
	}

	public Community getCommunity() {
		return community;
	}

	@Override /* TO DO */
	public boolean addMember(User whoCalledMethod, User newMember) {
		/* ensure that the new member is also a member of the community */
		if (newMember == null)
			return false;
		if (!community.currentMembers.contains(newMember))
			return false;
		return super.addMember(whoCalledMethod, newMember);
	}

	@Override
	public String toString() {
		return "[Channel in Community: " + community.name + "]" + super.toString();
	}

	@Override
	public String getFullInformation() {
		return "[Channel in Community: " + community.name + "]" + super.getFullInformation();
	}

	@Override /* TO DO */
	public boolean verify() {

		/* each User in current members is a member of the community */
		for (User w : currentMembers)
			if (!community.currentMembers.contains(w))
				return false;
		return super.verify();
	}
}

