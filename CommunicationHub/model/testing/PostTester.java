package model.testing;

import model.User;
import model.enumerations.GroupType;
import model.groups.Group;
import model.posts.Post;

public class PostTester {
	/* Just a tester for the formatting of a post */
	public static void main(String[] args) {
		User w = new User("Shawn", "Fiester", "876-222-1111");
		Group g = new Group(w, GroupType.MessagesToSelf, "", (short) 1, null);
		Post c = new Post(
				"Digital twins hold immense promise in accelerating scientific discovery, but the publicity currently outweighs the evidence base of success. We summarize key research opportunities in the computational sciences to enable digital twin technologies, as identified by a recent National Academies of Sciences, Engineering, and Medicine consensus study report.",
				w, g);
		System.out.println(c);
		System.out.println();

		Post d = new Post(
				"The publicity currently outweighs the evidence base of success. We summarize key research opportunities in the computational sciences to enable digital twin technologies, as identified by a recent National Academies of Sciences, Engineering, and Medicine consensus study report.",
				w, g, c);
		System.out.println(d);
		System.out.println();

		Post e = new Post(
				"We summarize key research opportunities in the computational sciences to enable digital twin technologies, as identified by a recent National Academies of Sciences, Engineering, and Medicine consensus study report.",
				w, g, d);
		System.out.println(e);
		System.out.println();
	}

}

