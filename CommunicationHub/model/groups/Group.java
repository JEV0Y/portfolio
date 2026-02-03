package model.groups;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import model.User;
import model.enumerations.Action;
import model.enumerations.GroupStatus;
import model.enumerations.GroupType;
import model.interfaces.FullInformation;
import model.interfaces.Verify;
import model.posts.Post;
import model.utilities.Pair;
import model.utilities.Tripple;

public class Group implements Comparable<Group>, FullInformation, Verify {

	public final static short MAX_GROUP_CAPACITY = 5, SELF_CAPACITY = 1, MIN_GROUP_CAPACITY = 2;

	private static long GroupIDs = 0;
	protected final long id;
	protected final String name;
	protected final User creator;
	protected final GroupType groupType;
	protected GroupStatus groupStatus;
	protected final short capacity;
	protected TreeSet<User> currentMembers;
	protected TreeSet<User> administrators;
	protected TreeSet<Post> conversation;

	/*
	 * For the audit log, we will use a list and not a set: while the order of
	 * elements is important we can have a Tripple in the list that is a duplicate
	 * that we must retain in the list, the last object may be a Pair.
	 */
	protected ArrayList<Tripple<Action, User, Object>> auditLog;

	public Group(User creator, GroupType groupType, String name, short capacity, Set<User> members) {
		this(creator, groupType, name, capacity, members, ++GroupIDs);
	}

	public Group(User creator, GroupType groupType, String name, short capacity, Set<User> members, long id) {
		this.id = id;
		// Ensure counter is at least this high
		if (id > GroupIDs)
			GroupIDs = id;

		this.creator = creator;
		this.groupType = groupType;
		this.groupStatus = GroupStatus.Active;

		this.capacity = isConversationwWithSelf() ? SELF_CAPACITY
				: isUserToUser() ? MIN_GROUP_CAPACITY
						: (capacity >= MIN_GROUP_CAPACITY && capacity <= MAX_GROUP_CAPACITY) ? capacity
								: capacity < MIN_GROUP_CAPACITY ? MIN_GROUP_CAPACITY : MAX_GROUP_CAPACITY;

		auditLog = new ArrayList<Tripple<Action, User, Object>>();
		administrators = new TreeSet<User>();
		conversation = new TreeSet<Post>();
		currentMembers = new TreeSet<User>();

		/*
		 * Initialize name before exposing 'this' to avoid NullPointerException
		 * when addGroupMembership triggers compareTo.
		 */
		String calculatedName = name;
		if (isConversationwWithSelf()) {
			calculatedName = creator.getFirstname() + " " + creator.getLastname();
		} else if (isUserToUser()) {
			TreeSet<User> temp = new TreeSet<>();
			temp.add(creator);
			if (members != null)
				temp.addAll(members);
			if (!temp.isEmpty()) {
				User first = temp.first();
				calculatedName = first.getFirstname() + " " + first.getLastname();
			}
		}
		this.name = calculatedName;

		/*
		 * need to add these directly and not use the methods because access control
		 * needs initialisation, i.e., we need at least one administrator
		 */
		currentMembers.add(creator);
		creator.addGroupMembership(this);
		addToLog(Action.Add, creator, creator);
		administrators.add(creator);
		addToLog(Action.Upgrade, creator, creator);

		/*
		 * now we can use apply access control to add the other members, so we can call
		 * the addMember method
		 */
		if (members != null)
			for (User w : members)
				if (!(w == creator)) /* because creator is already a member */
					addMember(creator, w);
	}

	public static void updateIDCounter(long maxID) {
		if (maxID > GroupIDs)
			GroupIDs = maxID;
	}

	private boolean isUserToUser() {
		return groupType == GroupType.UsertoUser;
	}

	private boolean isConversationwWithSelf() {
		return groupType == GroupType.MessagesToSelf;
	}

	public long getID() {
		return id;
	}

	public String getName() {
		return name;
	}

	public User getCreator() {
		return creator;
	}

	public GroupType getGroupType() {
		return groupType;
	}

	public short getCapacity() {
		return capacity;
	}

	public GroupStatus getGroupStatus() {
		return groupStatus;
	}

	public TreeSet<Post> getConversation() {
		return conversation;
	}

	private boolean addToLog(Action action, User who, Object what) {
		return auditLog.add(new Tripple<Action, User, Object>(action, who, what));
	}

	/* TO DO */
	public boolean isCreator(User whoCalledMethod) {
		if (whoCalledMethod == null)
			return false;
		return creator.compareTo(whoCalledMethod) == 0;
	}

	public boolean addMember(User whoCalledMethod, User newMember) {
		/*
		 * access control and conditions for success: if any of these is true, adding a
		 * member to the group is disallowed
		 */
		if (isUserToUser() || isConversationwWithSelf() || groupAtCapacity() || !isAdmin(whoCalledMethod))
			return false; /* no need to continue, the action was unsuccessful */

		/* Check if newMember was previously a member (cannot be re-added) */
		if (wasPreviousMember(whoCalledMethod, newMember))
			return false;

		/*
		 * add the new member to the current members: if successful, we know that added
		 * will have the value true, if unsuccessful, a reason could be that
		 * isCurrentMember(newMember) == true
		 * 
		 * Notice that we did not need to check isCurrentMember(newMember) in the access
		 * control because the TreeSet to store the currentMembers can detect a
		 * duplicate because it implements the Comparable interface and can detect when
		 * new Users do not have a unique phone number.
		 */
		boolean added = currentMembers.add(newMember);
		boolean addedToo = newMember.addGroupMembership(this);
		boolean addedThree = false;

		if (added && addedToo)
			addedThree = addToLog(Action.Add, whoCalledMethod, newMember);

		/* return whether the all the actions were successful or not. */
		return added && addedToo && addedThree;
	}

	/* TO DO */
	public boolean removeMember(User whoCalledMethod, User potential) {
		/* only an administrator may remove members and the group must be active */
		if (whoCalledMethod == null || potential == null)
			return false;
		if (groupStatus != GroupStatus.Active)
			return false;
		if (!isAdmin(whoCalledMethod))
			return false;
		/* cannot remove the creator of the group */
		if (creator.compareTo(potential) == 0)
			return false;
		/* can only remove current members */
		if (!currentMembers.contains(potential))
			return false;
		/* if the member is an admin, ensure at least one admin remains */
		boolean potentialIsAdmin = administrators.contains(potential);
		if (potentialIsAdmin && administrators.size() == 1)
			return false;
		boolean removedFromMembers = currentMembers.remove(potential);
		/* Update User state */
		boolean removedFromUser = potential.removeGroupMembership(this);

		boolean removedFromAdmins = true;
		if (potentialIsAdmin)
			removedFromAdmins = administrators.remove(potential);
		boolean logged = false;
		if (removedFromMembers && removedFromUser && removedFromAdmins)
			logged = addToLog(Action.Remove, whoCalledMethod, potential);
		return removedFromMembers && removedFromUser && removedFromAdmins && logged;
	}

	/* TO DO */
	public boolean leaveGroup(User leaver) {
		/* leaver must be a current member and group must be active */
		if (leaver == null)
			return false;
		if (groupStatus != GroupStatus.Active)
			return false;
		if (!currentMembers.contains(leaver))
			return false;
		/* creator is not allowed to leave their own group */
		if (creator.compareTo(leaver) == 0)
			return false;
		/* if leaver is an admin, ensure at least one admin remains */
		boolean leaverIsAdmin = administrators.contains(leaver);
		if (leaverIsAdmin && administrators.size() == 1)
			return false;
		boolean removedFromMembers = currentMembers.remove(leaver);
		/* Update User state */
		boolean removedFromUser = leaver.removeGroupMembership(this);

		boolean removedFromAdmins = true;
		if (leaverIsAdmin)
			removedFromAdmins = administrators.remove(leaver);
		boolean logged = false;
		if (removedFromMembers && removedFromUser && removedFromAdmins)
			logged = addToLog(Action.Leave, leaver, leaver);
		return removedFromMembers && removedFromUser && removedFromAdmins && logged;
	}

	/* TO DO */
	public boolean upgradeMemberToAdmin(User whoCalledMethod, User potential) {
		if (whoCalledMethod == null || potential == null)
			return false;
		if (groupStatus != GroupStatus.Active)
			return false;
		/* only an existing admin may promote another member */
		if (!isAdmin(whoCalledMethod))
			return false;
		/* potential must be a current member and not already an admin */
		if (!currentMembers.contains(potential) || administrators.contains(potential))
			return false;
		boolean added = administrators.add(potential);
		boolean logged = false;
		if (added)
			logged = addToLog(Action.Upgrade, whoCalledMethod, potential);
		return added && logged;
	}

	/* TO DO */
	public boolean downgradeAdminToRegularMember(User whoCalledMethod, User potential) {
		if (whoCalledMethod == null || potential == null)
			return false;
		if (groupStatus != GroupStatus.Active)
			return false;
		/* only an existing admin may demote another admin */
		if (!isAdmin(whoCalledMethod))
			return false;
		/* potential must currently be an administrator */
		if (!administrators.contains(potential))
			return false;
		/* must always have at least one administrator */
		if (administrators.size() == 1)
			return false;
		boolean removed = administrators.remove(potential);
		boolean logged = false;
		if (removed)
			logged = addToLog(Action.Downgrade, whoCalledMethod, potential);
		return removed && logged;
	}

	/* TO DO, incomplete */
	public boolean addPost(User whoCalledMethod, String message) {
		if (whoCalledMethod == null)
			return false;
		if (groupStatus != GroupStatus.Active)
			return false;
		/* only current members may post */
		if (!currentMembers.contains(whoCalledMethod))
			return false;

		conversation.add(new Post(message, whoCalledMethod, this));
		return true;
	}

	/* TO DO */
	public boolean addReply(User whoCalledMethod, long replyForPostID, String reply) {
		if (whoCalledMethod == null)
			return false;
		if (groupStatus != GroupStatus.Active)
			return false;
		/* only current members may reply */
		if (!currentMembers.contains(whoCalledMethod))
			return false;
		Post parent = null;
		for (Post p : conversation) {
			if (p.getID() == replyForPostID) {
				parent = p;
				break;
			}
		}
		if (parent == null)
			return false;
		boolean added = conversation.add(new Post(reply, whoCalledMethod, this, parent));
		if (added)
			addToLog(Action.Reply, whoCalledMethod, parent);
		return added;
	}

	/* TO DO, only admin allowed to do this */
	public boolean deactivateGroup(User whoCalledMethod) {
		if (whoCalledMethod == null)
			return false;
		/* only administrators may deactivate a group */
		if (!isAdmin(whoCalledMethod))
			return false;
		if (groupStatus == GroupStatus.Deactivated)
			return false;
		groupStatus = GroupStatus.Deactivated;
		addToLog(Action.Deactivate, whoCalledMethod, this);
		return true;
	}

	/* TO DO, incomplete: apply access control, only current members authorised */
	public boolean isCurrentMember(User whoCalledMethod, User potential) {
		if (whoCalledMethod == null || potential == null)
			return false;
		/* only current members are authorised to query membership */
		if (!currentMembers.contains(whoCalledMethod))
			return false;
		for (User w : currentMembers)
			if (w.compareTo(potential) == 0)
				return true;
		return false;
	}

	/* TO DO, incomplete: apply access control, only current members authorised */
	public boolean wasPreviousMember(User whoCalledMethod, User potential) {
		if (whoCalledMethod == null || potential == null)
			return false;
		/* only current members are authorised */
		if (!currentMembers.contains(whoCalledMethod))
			return false;
		/*
		 * a previous member is someone that appears in the audit log but is not
		 * currently in the set of currentMembers
		 */
		boolean everMember = false;
		for (Tripple<Action, User, Object> t : auditLog) {
			if (t.second.compareTo(potential) == 0) {
				everMember = true;
				break;
			}
			if (t.third instanceof User) {
				if (((User) t.third).compareTo(potential) == 0) {
					everMember = true;
					break;
				}
			}
		}
		return everMember && !currentMembers.contains(potential);
	}

	/* TO DO */
	public boolean isAdmin(User whoCalledMethod) {
		if (whoCalledMethod == null)
			return false;
		return administrators.contains(whoCalledMethod);
	}

	/* TO DO */
	private boolean groupAtCapacity() {
		return currentMembers.size() >= capacity;
	}

	@Override
	public String toString() {
		return name + " (Type: " + groupType.getDescription() + ", Members: " + currentMembers.size() + ")";
	}

	@Override
	public int compareTo(Group o) {
		int nameComp = name.compareTo(o.getName());
		if (nameComp != 0)
			return nameComp;
		return Long.compare(id, o.getID());
	}

	@Override
	public String getFullInformation() {
		String str = name + " (Type: " + groupType.getDescription() + ")\n";
		str += "Current members: " + currentMembers.size() + "\n";

		TreeSet<String> cm = new TreeSet<String>();
		for (User w : currentMembers)
			cm.add(w.toString());
		for (String w : cm)
			str += "\t" + w + "\n";

		cm.clear();
		for (Pair<Action, User> p : auditLog)
			if (!currentMembers.contains(p.second))
				cm.add(p.second.toString());
		str += "Past members: " + cm.size() + "\n";
		for (String w : cm)
			str += "\t" + w + "\n";

		str += "Posts: " + conversation.size() + "\n";
		for (Post p : conversation)
			str += p.getFullInformation() + "\n";
		return str;
	}

	@Override /* TO DO */
	public boolean verify() {
		/*
		 * each User in current members has themself as a member of the group
		 */
		for (User w : currentMembers) {
			if (!w.isMemberOfGroup(this))
				return false;
		}

		/*
		 * each User in administator is a current member of the group
		 */
		for (User admin : administrators) {
			if (!currentMembers.contains(admin))
				return false;
		}

		/*
		 * each of the posts in the conversation can be attributed to current or
		 * historical members of
		 * the group
		 */
		for (Post p : conversation) {
			model.PhoneNumber posterPhone = p.getPostedBy();
			boolean isCurrent = false;
			for (User w : currentMembers) {
				if (w.getPhoneNumber().compareTo(posterPhone) == 0) {
					isCurrent = true;
					break;
				}
			}

			boolean isHistorical = false;
			// Check historical members from audit log
			for (Tripple<Action, User, Object> t : auditLog) {
				if (t.second.getPhoneNumber().compareTo(posterPhone) == 0) {
					isHistorical = true;
					break;
				}
				if (t.third instanceof User) {
					if (((User) t.third).getPhoneNumber().compareTo(posterPhone) == 0) {
						isHistorical = true;
						break;
					}
				}
			}
			if (!isCurrent && !isHistorical)
				return false;
		}

		/*
		 * no post in the conversation is an announcement
		 */
		for (Post p : conversation) {
			if (p instanceof model.posts.Announcement)
				return false;
		}

		/*
		 * if a User leaves, the group or is removed, they cannot be added again to the
		 * group
		 */
		// This is implicitly checked by addMember logic, but to verify state:
		// Check if any current member is also in the "removed/left" history?
		// Actually, the requirement says "cannot be added again".
		// The verification should check if any CURRENT member is in the list of people
		// who left/removed.
		// But we don't have a separate list for "left/removed" in Group, only auditLog.
		// So we check if any current member has a Leave or Remove action in the past?
		// But wait, if they were added, then left, then added again (which is
		// forbidden),
		// the audit log would show Add -> Leave -> Add.
		// So we need to check if any current member has a 'Leave' or 'Remove' event in
		// the log for this group.
		// However, the audit log stores (Action, Who, What). For Leave/Remove, 'What'
		// is the User.

		for (User member : currentMembers) {
			for (Tripple<Action, User, Object> entry : auditLog) {
				if ((entry.first == Action.Leave || entry.first == Action.Remove)) {
					// The third element (what) is the User who left or was removed
					if (entry.third instanceof User && ((User) entry.third).compareTo(member) == 0) {
						return false; // Should not be a current member if they left/removed previously
					}
				}
			}
		}

		return true;
	}

}

