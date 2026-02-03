package model;

/*
 * Review the javadoc for TreeSet to understand how a TreeSet is
 * maintained, including when an operation would try to add a duplicate.
 */
import java.util.TreeSet;
import model.interfaces.FullInformation;

public class CommunicationHub implements FullInformation {

	private TreeSet<User> registeredUsers;

	/*
	 * The set of Users will be ordered by the natural ordering for Users, i.e.,
	 * PhoneNumber; it uses the Comparable interface for imposing a natural ordering
	 * of the collection.
	 */
	public CommunicationHub() {
		registeredUsers = new TreeSet<User>();
	}

	/*
	 * creating a User with the same phone number of an existing User will fail,
	 * i.e., cause a value of false to be returned.
	 */
	public boolean register(String firstname, String lastname, String phoneNumber) {
		if (PhoneNumber.isValid(phoneNumber) && firstname != null && lastname != null)
			return registeredUsers.add(new User(firstname, lastname, phoneNumber));
		return false;
	}

	/* TO DO */
	public TreeSet<User> findUser(String name) {
		TreeSet<User> Users = new TreeSet<User>();
		if (name == null || name.isEmpty()) return Users;
		for (User w : registeredUsers){
			if (w.getFullName().equals(name)){
				Users.add(w);
			}
		}
			return Users;
	}

	/* TO DO */
	public User findUser(PhoneNumber phoneNumber) {
		if (phoneNumber == null) return null;
		for (User w : registeredUsers){
			if (w.getPhoneNumber().compareTo(phoneNumber) == 0){
				return w;
			}
		}
		return null;
	}

	/* TO DO */
	public TreeSet<User> findUsersWithNameSubString(String nameSubstring) {
		TreeSet<User> Users = new TreeSet<User>();
		if (nameSubstring == null || nameSubstring.isEmpty()) return Users;
		for (User w : registeredUsers){
			if (w.getFullName().contains(nameSubstring)){
				Users.add(w);
			}
		}
		return Users;
	}

	/* TO DO */
	public TreeSet<User> findUserWithPhonePrefix(String prefix) {
		TreeSet<User> Users = new TreeSet<User>();
		if (prefix == null || prefix.isEmpty()) return Users;
		for (User w : registeredUsers) {
			if (w.getPhoneNumber().toString().startsWith(prefix)) {
				Users.add(w);
			}
		}
		return Users;
	}

	/* TO DO */
	public TreeSet<User> findUserWithPhonePostfix(String postfix) {
		TreeSet<User> Users = new TreeSet<User>();
		if (postfix == null || postfix.isEmpty()) return Users;
		for (User w : registeredUsers) {
			if (w.getPhoneNumber().toString().endsWith(postfix)) {
				Users.add(w);
			}
		}
		return Users;
	}

	/* TO DO */
	public TreeSet<User> findUserWithPhoneSubString(String phoneSubString) {
		TreeSet<User> Users = new TreeSet<User>();
		if (phoneSubString == null || phoneSubString.isEmpty()) return Users;
		for (User w : registeredUsers) {
			if (w.getPhoneNumber().toString().contains(phoneSubString)) {
				Users.add(w);
			}
		}
		return Users;
	}

	@Override
	public String getFullInformation() {
		String str = "Number of Users: " + registeredUsers.size() + "\n";
		for (User w : registeredUsers)
			str += "\n" + w.getFullInformation();
		return str;
	}
	
	/* load some initial data into CommunicationHub */
	public void initialise() {}

    /* Expose a copy of registered Users for read-only consumption by the GUI */
    public TreeSet<User> getRegisteredUsers() {
        return new TreeSet<User>(registeredUsers);
    }
}


