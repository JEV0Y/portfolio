package project;

import project.enums.PageAlgorithm;
import project.enums.PostAudience;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.Collections;

/**
 * Represents a social media platform instance in the SocialConnect application.
 * This class manages users, posts, and interactions between users.
 * It provides functionality for user authentication, post management, and user
 * relationships.
 */
public class SocialConnect {
    /** Name of the social media platform (minimum 8 characters) */
    private String name;

    /** Algorithm used for sorting posts on user pages */
    private PageAlgorithm sortBy;

    /** Currently logged-in user */
    private User whoIsLoggedIn;

    /** List of all registered users */
    private ArrayList<User> members;

    /** List of all posts made on the platform */
    private ArrayList<Post> posts = new ArrayList<>(); // Initialize posts here

    /**
     * Creates a new SocialConnect platform with the specified name and sorting
     * algorithm.
     * 
     * @param name   The name of the platform
     * @param sortBy The algorithm to use for sorting posts
     * @throws IllegalArgumentException if name is less than 8 characters
     */
    public SocialConnect(String name, PageAlgorithm sortBy) {
        if (name.length() < 8) {
            throw new IllegalArgumentException("Platform name must be at least 8 characters long");
        }
        this.name = name;
        this.sortBy = sortBy;
        this.members = new ArrayList<>();
    }

    /**
     * Authenticates a user and logs them into the platform.
     *
     * @param username The username of the user trying to log in
     * @param password The password for authentication
     * @return true if login successful, false otherwise
     */
    public boolean login(String username, String password) {
        for (User member : members) {
            if (member.getUsername().equals(username) && member.getPassword().equals(password)) {
                this.whoIsLoggedIn = member;
                member.setLoggedInto(this); // Set the SocialConnect instance in the User
                return true;
            }
        }
        return false;
    }

    /**
     * Logs out the currently logged-in user.
     */
    public void logout() {
        if (whoIsLoggedIn != null) {
            whoIsLoggedIn.logout();
            whoIsLoggedIn = null;
        }
    }

    /**
     * Returns the currently logged-in user.
     *
     * @return The currently logged-in user, or null if no user is logged in
     */
    public User getWhoIsLoggedIn() {
        return whoIsLoggedIn;
    }

    /**
     * Returns the list of all posts made on the platform.
     *
     * @return The list of all posts
     */
    public ArrayList<Post> getPosts() {
        return posts;
    }

    /**
     * Returns the list of all registered users.
     *
     * @return The list of all registered users
     */
    public ArrayList<User> getMembers() {
        return new ArrayList<>(members);
    }

    /**
     * Registers a new user with the given username and password.
     *
     * @param username The username for the new user
     * @param password The password for the new user
     * @return true if registration successful, false if username is already taken
     */
    public boolean register(String username, String password) {
        for (User member : members) {
            if (member.getUsername().equals(username)) {
                return false;
            }
        }
        members.add(new User(username, password));
        return true;
    }

    /**
     * Returns a string representation of the SocialConnect object.
     * This includes the social network's name, the page sorting algorithm,
     * the user currently logged in (if any), the number of members,
     * and the number of posts. It also lists details of each member
     * using their own toString method.
     *
     * @return A formatted string representing the SocialConnect object.
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("SocialConnect [name=" + name + ", sortBy=" + sortBy.getDescription() + ", whoIsLoggedIn=");
        if (whoIsLoggedIn != null) {
            sb.append(whoIsLoggedIn.toString());
        } else {
            sb.append("null");
        }
        sb.append(", members=" + members.size() + ", posts=" + posts.size() + "]\n\n");
        for (User member : members) {
            sb.append(member.toString());
        }
        return sb.toString();

        /*
         * regenerate this so that it is nicely formatted. ensure that you call the
         * toString for each member
         * return "SocialConnect [socialsName=" + socialsName + ", pageSort=" +
         * pageSort.getDescription() + ", whoIsLoggedIn="
         * + whoIsLoggedIn + ", members=" + members.size() + ", posts=" + posts.size() +
         * "]\n\n" + members;
         */
    }

    /**
     * Returns the name of this social network.
     * 
     * @return The name of this social network.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the page sorting algorithm used by this social network.
     * 
     * @return The page sorting algorithm used by this social network.
     */
    public PageAlgorithm getSortBy() {
        return sortBy;
    }

    /**
     * Compares two Posts based on their post IDs.
     */
    class SortByID implements Comparator<Post> {

        /**
         * Compares two Posts based on their post IDs.
         * 
         * @param a The first Post.
         * @param b The second Post.
         * @return A negative integer, zero, or a positive integer as the post ID
         *         of the first Post is less than, equal to, or greater than the
         *         post ID of the second Post.
         */
        public int compare(Post a, Post b) {

            return ((Integer) a.getPostID()).compareTo(b.getPostID());
        }
    }

    /**
     * Compares two Posts based on their popularity scores.
     */
    class SortByPopularity implements Comparator<Post> {

        /**
         * Compares two Posts based on their popularity scores.
         * 
         * @param a The first Post.
         * @param b The second Post.
         * @return A negative integer, zero, or a positive integer as the
         *         popularity score of the first Post is less than, equal to, or
         *         greater than the popularity score of the second Post.
         */
        public int compare(Post a, Post b) {

            return ((Integer) a.getPopularityScore()).compareTo(b.getPopularityScore());

        }

        /**
         * Adds a new post to the platform.
         * 
         * @param post The post to add.
         */
        public void addPost(Post post) {
            posts.add(post);
        }

        /**
         * Adds a new member to the platform.
         * 
         * @param user The user to add.
         */
        public void addMember(User user) {
            members.add(user);
        }

    }

    /**
     * Logs a new post to the platform.
     * 
     * @param post The post to add
     */
    public void logNewPost(Post post) {
        if (post != null) {
            posts.add(post);
        }
    }

    /**
     * Checks if a username is already taken by another user.
     * 
     * @param username The username to check.
     * @return true if the username is taken, false otherwise.
     */
    public boolean isUsernameTaken(String username) {
        for (User member : members) {
            if (member.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a new member to the platform with the given username and password.
     * 
     * @param username The username for the new member.
     * @param password The password for the new member.
     * @return true if the member was added successfully, false if the username is
     *         already taken.
     */
    public boolean addNewMember(String username, String password) {
        if (isUsernameTaken(username)) {
            return false;
        }
        User newUser = new User(username, password);
        members.add(newUser);
        return true;
    }

    /**
     * Removes a member from the platform.
     * 
     * @param username The username of the member to remove.
     * @return true if the member was removed successfully, false if the member does
     *         not exist.
     */
    public boolean removeMember(String username) {
        for (User member : members) {
            if (member.getUsername().equals(username)) {
                members.remove(member);
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a user is a member of the platform.
     * 
     * @param username The username to check.
     * @return true if the user is a member, false otherwise.
     */
    public boolean isMember(String username) {
        for (User member : members) {
            if (member.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a user to the platform.
     * 
     * @param user The user to add.
     */
    public void addUser(User user) {
        if (user != null && !members.contains(user)) {
            members.add(user);
        }
    }

    /**
     * Deletes a post from the platform.
     * 
     * @param post The post to delete.
     * @throws IllegalArgumentException if the post does not exist on the platform.
     */
    public void deletePost(Post post) {
        if (post == null) {
            throw new IllegalArgumentException("Post cannot be null");
        }

        // Remove from posts list
        if (!posts.remove(post)) {
            throw new IllegalArgumentException("Post does not exist in SocialConnect");
        }

        // Remove from user's owned posts
        User owner = null;
        for (User member : members) {
            if (member.getUsername().equals(post.getOwner())) {
                owner = member;
                break;
            }
        }

        if (owner != null) {
            owner.removePost(post);
        }
    }

    /**
     * Returns the posts made by a specific user.
     * 
     * @param username The username of the user whose posts to retrieve.
     * @return The list of posts made by the user.
     */
    public List<Post> getUserPosts(String username) {
        List<Post> userPosts = new ArrayList<>();
        for (Post post : posts) {
            if (post.getOwner().equals(username)) {
                userPosts.add(post);
            }
        }
        return userPosts;
    }

    /**
     * Sorts and returns visible posts according to the current page algorithm.
     * 
     * @param postsToSort List of posts to sort
     * @return Sorted list of posts
     */
    private ArrayList<Post> sortPosts(ArrayList<Post> postsToSort) {
        ArrayList<Post> sortedPosts = new ArrayList<>(postsToSort);

        switch (sortBy) {
            case Popular:
                Collections.sort(sortedPosts, new SortByPopularity());
                Collections.reverse(sortedPosts); // Highest popularity first
                break;
            case Oldest:
                Collections.sort(sortedPosts, new SortByID());
                break;
            case Newest:
                Collections.sort(sortedPosts, new SortByID());
                Collections.reverse(sortedPosts);
                break;
        }

        return sortedPosts;
    }

    /**
     * Returns the posts that are visible to a specific user, sorted according to
     * the current algorithm.
     * 
     * @param viewer The user who is viewing the posts
     * @return The sorted list of visible posts
     */
    public List<Post> getVisiblePosts(User viewer) {
        ArrayList<Post> visiblePosts = new ArrayList<>();

        // Filter posts based on visibility
        for (Post post : posts) {
            if (post.canView(viewer)) {
                visiblePosts.add(post);
            }
        }

        return sortPosts(visiblePosts);
    }

    /**
     * Sets the page sorting algorithm for the platform.
     * 
     * @param algorithm The new page sorting algorithm.
     */
    public void setSortBy(PageAlgorithm algorithm) {
        this.sortBy = algorithm;
    }

    /**
     * Returns a user with the given username.
     * 
     * @param username The username of the user to retrieve.
     * @return The user with the given username, or null if no such user exists.
     */
    public User getUser(String username) {
        if (username == null || username.isEmpty()) {
            return null;
        }
        for (User member : members) {
            if (member.getUsername().equals(username)) {
                return member;
            }
        }
        return null;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // Create a new SocialConnect instance
        SocialConnect SocialConnect = new SocialConnect("My SocialConnect", PageAlgorithm.Popular);

        // Create some users
        User user1 = new User("username1", "password1");
        User user2 = new User("username2", "password2");
        User user3 = new User("username3", "password3");

        // Add users to SocialConnect
        SocialConnect.members.add(user1);
        SocialConnect.members.add(user2);
        SocialConnect.members.add(user3);

        // Create a post
        RegularPost post1 = user1.createPost("Hello, world!", PostAudience.Followers);
        RegularPost post2 = user2.createPost("Hello, world!", PostAudience.Followers);
        RegularPost post3 = user3.createPost("Hello, world!", PostAudience.Followers);

        // Add post to SocialConnect
        SocialConnect.posts.add(post1);
        SocialConnect.posts.add(post2);
        SocialConnect.posts.add(post3);

        // Print SocialConnect details
        System.out.println(SocialConnect.toString());

        user1.startFollowing("username2");
        user2.startFollowing("username3");
        user3.startFollowing("username1");
    }

    /**
     * Creates a new post using speech recognition for the currently logged-in user.
     * 
     * @param audience           The visibility setting for the post
     * @param maxDurationSeconds Maximum recording duration in seconds
     * @return The newly created post, or null if no user is logged in
     * @throws Exception If there's an error during speech recognition
     */
    /*
     * Speech recognition feature disabled due to missing dependency.
     *
     * public Post createPostFromSpeech(PostAudience audience, int
     * maxDurationSeconds) throws Exception {
     * if (whoIsLoggedIn == null) {
     * throw new IllegalStateException("No user is logged in");
     * }
     * 
     * RegularPost post = whoIsLoggedIn.createPostFromSpeech(audience,
     * maxDurationSeconds);
     * logNewPost(post);
     * return post;
     * }
     */
}
