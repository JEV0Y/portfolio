package model.posts;

import model.User;
import model.groups.Group;

public class Announcement extends Post {

	public Announcement(String message, User postedBy, Group postedIn) {
		super(message, postedBy, postedIn);
	}

	public Announcement(String message, User postedBy, Group postedIn, long id) {
		super(message, postedBy, postedIn, null, id);
	}

	@Override
	public Long getReplyFor() {
		return (long) -1; // method body is complete, do not change. no post should have id == -1
	}

	@Override /* TO DO */
	public boolean verify() {
		/*
		 * is not a reply
		 * 
		 */

		return false;
	}
}

