package model.posts;

import model.PhoneNumber;
import model.User;
import model.groups.Group;
import model.groups.Community;
import model.interfaces.FullInformation;
import model.interfaces.Verify;
import model.utilities.StringWrapper;

public class Post implements Comparable<Post>, FullInformation, Verify {
	private static long IDs = 0;
	protected final static short WRAP_WIDTH = 65;
	protected final long id;
	protected final String text;
	protected final User poster;
	protected final Group postedIn;
	private final Post isReplyFor;

	public Post(String message, User postedBy, Group postedIn) {
		this(message, postedBy, postedIn, null, ++IDs);
	}

	public String getText() {
		return text;
	}

	public Post(String message, User postedBy, Group postedIn, Post replyFor) {
		this(message, postedBy, postedIn, replyFor, ++IDs);
	}

	public Post(String message, User postedBy, Group postedIn, Post replyFor, long id) {
		this.id = id;
		if (id > IDs)
			IDs = id;

		this.text = (message == null ? "" : message);
		poster = postedBy;
		this.postedIn = postedIn;
		isReplyFor = replyFor;
	}

	public static void updateIDCounter(long maxID) {
		if (maxID > IDs)
			IDs = maxID;
	}

	@Override
	public String toString() {

		return (isReplyFor == null ? "" : "\t(Is Reply for message with ID: " + isReplyFor.getID() + ")\n")
				+ StringWrapper.wrapString(
						"(ID: " + id + ", Posted by: " + poster.getFirstname() + " " + poster.getLastname() + " ) "
								+ text,
						WRAP_WIDTH,
						true);
	}

	public long getLastID() {
		return IDs;
	}

	public long getID() {
		return id;
	}

	public Long getReplyFor() {
		return isReplyFor.getID();
	}

	public Long getGroupPostedIn() {
		return postedIn.getID();
	}

	public PhoneNumber getPostedBy() {
		return poster.getPhoneNumber();
	}

	@Override
	public String getFullInformation() {
		return toString();
	}

	@Override
	public int compareTo(Post o) {
		return ((Long) id).compareTo(o.getID());
	}

	@Override /* TO DO */
	public boolean verify() {
		/*
		 * isReplyTo is not of type announcement
		 * 
		 * poster is a currentMember or historical member of the group
		 */

		/*
		 * if this is a reply, ensure the post replied to is not itself an announcement
		 */
		if (isReplyFor != null && isReplyFor instanceof model.posts.Announcement)
			return false;
		/* poster must be a current or previous member of the group */
		if (poster == null || postedIn == null)
			return false;
		boolean member = postedIn.isCurrentMember(poster, poster) || postedIn.wasPreviousMember(poster, poster);
		return member;
	}

}

