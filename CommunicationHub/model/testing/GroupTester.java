package model.testing;

import model.User;
import model.enumerations.GroupType;
import model.groups.Group;
import model.posts.Post;

public class GroupTester {

	public static void main(String[] args) {
		User r = new User("Phil", "Jackson", "879-111-0000"), s = new User("Jenn", "Phipps", "876-121-2222"),
				t = new User("Amari", "Apple", "876-131-0010"), w = new User("Shawn", "Fiester", "876-222-1111");
		Group g = new Group(w, GroupType.RegularGroup, "COMP1161 Group test", (short) 3, null);
		g.addMember(w, r);
		g.addMember(w, t);
		g.addMember(w, s);
		String str1 = "Digital twins hold immense promise in accelerating scientific discovery, "
				+ " but the publicity currently outweighs the evidence base of success.";
		String str2 = "We summarize key research opportunities in the computational sciences "
				+ "to enable digital twin technologies, as identified by a recent National "
				+ "Academies of Sciences, Engineering, and Medicine consensus study report.";
		String str3 = "The National Fisheries Authority (NFA) has announced a six-month moratorium "
				+ "on licence and registration fees for fishers severely impacted by Hurricane Melissa.";
		g.addPost(w, str3);
		g.addPost(t, str2);
		g.addPost(r, str1);
		System.out.println(g.getFullInformation());

	}

}

