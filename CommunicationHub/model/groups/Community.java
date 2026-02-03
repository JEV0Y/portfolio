package model.groups;

import java.util.Set;
import java.util.TreeSet;

import model.User;
import model.enumerations.GroupType;
import model.posts.Announcement;

public class Community extends Group {

	private TreeSet<Channel> channels;

	public Community(User creator, String name, Set<User> members) {
		super(creator, GroupType.RegularGroup, name, Group.MAX_GROUP_CAPACITY, members);
		channels = new TreeSet<Channel>();
	}

	public Community(User creator, String name, Set<User> members, long id) {
		super(creator, GroupType.RegularGroup, name, Group.MAX_GROUP_CAPACITY, members, id);
		channels = new TreeSet<Channel>();
	}

	/*
	 * /* TO DO, a channel is a regular group with Group.MAX_GROUP_CAPACITY capacity
	 */
	public boolean addChannel(User whoCalledMethod, String channelName, Set<User> members) {
		if (whoCalledMethod == null || channelName == null)
			return false;
		/* only administrators in the community may create channels */
		if (!isAdmin(whoCalledMethod))
			return false;
		Channel c = new Channel(this, whoCalledMethod, channelName, members);
		return channels.add(c);
	}

	@Override /* TO DO */
	public boolean addPost(User whoCalledMethod, String message) {
		/* in a community, all messages in the conversation must be announcements */
		if (whoCalledMethod == null)
			return false;
		/* only administrators in the community may post announcements */
		if (!isAdmin(whoCalledMethod))
			return false;

		conversation.add(new Announcement(message, whoCalledMethod, this));
		return true;
	}

	@Override
	public boolean addReply(User whoCalledMethod, long replyForPostID, String reply) {
		return false; /* this method is complete, do not change it */
	}

	@Override
	public String toString() {
		return "Community: " + super.toString() + "\n" + "Channels: " + channels.size();
	}

	@Override
	public String getFullInformation() {
		String str = "Community: " + super.getFullInformation();

		str += "Channels: " + channels.size();
		for (Channel c : channels)
			str += "\n" + c.toString() + "\n";

		return str;
	}

	@Override /* TO DO */
	public boolean verify() {

		/* conversation only contain announcements */
		boolean onlyAnnouncements = true;
		for (model.posts.Post p : conversation) {
			if (!(p instanceof Announcement)) {
				onlyAnnouncements = false;
				break;
			}
		}
		return onlyAnnouncements && super.verify();
	}
}

