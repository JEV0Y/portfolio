/**
 * Represents a user in the SocialConnect platform.
 * This class manages user authentication, relationships, and post operations.
 * 
 * @author 
 * 
 */
package project;

import project.enums.PostAudience;
import project.interfaces.Displayable;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

/**
 * A User object represents an individual user in the SocialConnect platform.
 * This class provides methods for managing user authentication, relationships,
 * and post operations.
 */
public class User implements Comparable<User>, Displayable {
    private String username;// cannot be empty. at least 8 characters long. must be checked before calling
                            // constructor.
    private String password; // cannot be empty. at least 8 characters long. must be checked before calling
                             // constructor.
    private ArrayList<Post> owned;
    private ArrayList<Post> posts; // List to store all posts (owned + reshared)
    private ArrayList<String> followers; // should not contain duplicated nor the this.username
    private ArrayList<String> following; // should not contain duplicates nor the this.username
    private SocialConnect loggedInto;

    /**
     * Constructs a new User with the specified username and password.
     * Both username and password must be at least 8 characters long.
     *
     * @param username The unique identifier for the user (minimum 8 characters)
     * @param password The user's password (minimum 8 characters)
     * @throws IllegalArgumentException if username or password is null, empty, or
     *                                  less than 8 characters
     */
    public User(String username, String password) {
        if (username == null || username.isEmpty() || username.length() < 8) {
            throw new IllegalArgumentException("Username must be at least 8 characters long.");
        }
        if (password == null || password.isEmpty() || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long.");
        }
        this.username = username;
        this.password = password;
        this.owned = new ArrayList<Post>();
        this.posts = new ArrayList<Post>(); // Initialize posts list
        this.followers = new ArrayList<String>();
        this.following = new ArrayList<String>();
        this.loggedInto = null;
    }

    /**
     * Returns the username of this User object.
     * 
     * @return The unique username identifier of this user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the password of this User object.
     * 
     * @return The user's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Returns a copy of the list of posts owned by this user.
     * Modifications to the returned list do not affect the user's actual posts.
     * 
     * @return A new ArrayList containing all posts owned by this user
     */
    public ArrayList<Post> getOwnedPosts() {
        return new ArrayList<>(owned);
    }

    /**
     * Returns a copy of the list of usernames of people following this User object.
     * The returned list is modifiable and does not support null elements.
     * 
     * @return A copy of the list of usernames of people following this User object
     */
    public ArrayList<String> getFollowers() {
        return new ArrayList<>(followers);
    }

    /**
     * Returns a copy of the list of usernames of people this User object is
     * following.
     * The returned list is modifiable and does not support null elements.
     * 
     * @return A copy of the list of usernames of people this User object is
     *         following
     */
    public ArrayList<String> getFollowing() {
        return new ArrayList<>(following);
    }

    /**
     * Compares this User object with another User object based on their usernames.
     * This comparison is case sensitive.
     * 
     * @param o The User object to compare this object with
     * @return A negative integer, zero, or a positive integer as this object is
     *         less than, equal to, or greater than the object o
     */
    @Override
    public int compareTo(User o) {
        // Compare using username - case sensitive as specified
        return this.username.compareTo(o.username);
    }

    /**
     * Sets the SocialConnect platform instance this user is currently logged into.
     * 
     * @param SocialConnect The SocialConnect platform instance, or null if logging
     *                      out
     */
    public void setLoggedInto(SocialConnect SocialConnect) {
        this.loggedInto = SocialConnect;
    }

    /**
     * Logs out the user from the current SocialConnect platform instance.
     */
    public void logout() {
        loggedInto = null;
    }

    /**
     * Creates a new regular post with the specified text and audience.
     * 
     * @param text     The content text of the post
     * @param audience The visibility setting for the post
     * @return The newly created RegularPost
     * @throws IllegalArgumentException if text is null or empty
     */
    public RegularPost createPost(String text, PostAudience audience) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Post text cannot be empty");
        }
        RegularPost post = new RegularPost(username, text, audience);
        this.owned.add(post);
        this.posts.add(post);
        return post;
    }

    /**
     * Creates a reshared post from an existing post.
     * 
     * @param originalPost The post to be reshared
     * @param audience     The visibility setting for the reshared post
     * @return The newly created ResharedPost
     * @throws IllegalArgumentException if originalPost is null, not viewable by
     *                                  this user,
     *                                  or if attempting to share with broader
     *                                  audience than original
     */
    public ResharedPost resharePost(Post originalPost, PostAudience audience) {
        if (originalPost == null) {
            throw new IllegalArgumentException("Original post cannot be null");
        }

        // Check if user can view the original post
        if (!originalPost.canView(this)) {
            throw new IllegalArgumentException("Cannot reshare a post you cannot view");
        }

        // Check audience restrictions
        if (audience.ordinal() > originalPost.getAudience().ordinal()) {
            throw new IllegalArgumentException("Cannot share with broader audience than original post");
        }

        ResharedPost post = new ResharedPost(audience.toString(), originalPost.getPostID(), originalPost,
                this.username);
        this.owned.add(post);
        this.posts.add(post);
        return post;
    }

    /**
     * Creates a new image post with the specified caption, image path, and
     * audience.
     * 
     * @param caption   The caption for the image
     * @param imagePath The path to the image file
     * @param audience  The audience setting for this post
     * @return The created ImagePost
     */
    public void createImagePost(String caption, String imagePath, PostAudience audience) {
        try {
            ImagePost post = new ImagePost(audience, username, imagePath, caption);
            posts.add(post);
            if (loggedInto != null) {
                loggedInto.logNewPost(post);
            }
        } catch (IOException e) {
            System.err.println("Error creating image post: " + e.getMessage());
        }
    }

    /**
     * Retrieves posts visible to a specific viewer, sorted according to the
     * specified criteria.
     * 
     * @param sortBy The sorting criterion ("popularity", "newest", or "oldest")
     * @param viewer The user attempting to view the posts
     * @return ArrayList of posts visible to the viewer, sorted as specified
     * @throws IllegalArgumentException if sortBy is not one of the valid options
     */
    public ArrayList<Post> getPosts(String sortBy, User viewer) {
        ArrayList<Post> visiblePosts = new ArrayList<>();

        // Filter posts based on viewer's permissions
        for (Post post : posts) {
            if (post.canView(viewer)) {
                visiblePosts.add(post);
            }
        }

        // Sort posts based on preference
        switch (sortBy.toLowerCase()) {
            case "popularity":
                visiblePosts.sort((p1, p2) -> p2.getPopularityScore() - p1.getPopularityScore());
                break;
            case "newest":
                visiblePosts.sort((p1, p2) -> p2.getPostID() - p1.getPostID());
                break;
            case "oldest":
                visiblePosts.sort((p1, p2) -> p1.getPostID() - p2.getPostID());
                break;
            default:
                throw new IllegalArgumentException("Invalid sort option. Use: popularity, newest, or oldest");
        }

        return visiblePosts;
    }

    /**
     * Deletes a post owned by this user.
     * 
     * @param post The post to be deleted
     * @throws IllegalArgumentException if post is null or not owned by this user
     */
    public void deletePost(Post post) {
        if (post == null) {
            throw new IllegalArgumentException("Post cannot be null");
        }
        if (!owned.contains(post)) {
            throw new IllegalArgumentException("Post does not exist or cannot be deleted");
        }
        // Remove from owned posts
        owned.remove(post);
        // Remove from posts
        posts.remove(post);
        // Remove from SocialConnect if logged in
        if (loggedInto != null) {
            loggedInto.deletePost(post);
        }
    }

    /**
     * Removes a post from this user's owned posts.
     * 
     * If the given post is null, this method does nothing.
     * 
     * @param post The post to remove from this user's owned posts.
     */
    void removePost(Post post) {
        if (post != null) {
            owned.remove(post);
            posts.remove(post);
        }
    }

    /**
     * Adds a follower to the set of people following this user.
     * 
     * @param followerUsername The username of the person to add as a follower.
     * @throws IllegalArgumentException if the follower username is null, empty, or
     *                                  the same as the
     *                                  current user's username.
     */
    public void addFollower(String followerUsername) {
        if (followerUsername == null || followerUsername.isEmpty()) {
            throw new IllegalArgumentException("Follower username cannot be empty");
        }
        if (followerUsername.equals(this.username)) {
            throw new IllegalArgumentException("Cannot follow yourself");
        }
        if (!followers.contains(followerUsername)) {
            followers.add(followerUsername);
        }
    }

    /**
     * @param followerUsername
     */
    public void removeFollower(String followerUsername) {
        if (followerUsername == null || followerUsername.isEmpty()) {
            throw new IllegalArgumentException("Follower username cannot be empty");
        }
        followers.remove(followerUsername);
    }

    /**
     * @param followerUsername
     * @return boolean
     */
    public boolean isAFollower(String followerUsername) {
        return followers.contains(followerUsername);
    }

    public void startFollowing(String followingUsername) {
        if (followingUsername == null || followingUsername.isEmpty()) {
            throw new IllegalArgumentException("Following username cannot be empty");
        }
        if (followingUsername.equals(this.username)) {
            throw new IllegalArgumentException("Cannot follow yourself");
        }

        if (!following.contains(followingUsername)) {
            following.add(followingUsername);
        }
    }

    /**
     * Stops following a user with the given username.
     * 
     * This method removes the specified username from the list of users
     * this User is following. If the user is logged into a SocialConnect
     * instance, it also removes this User from the other user's list
     * of followers.
     * 
     * @param followingUsername The username of the user to stop following.
     * @throws IllegalArgumentException if the username is null or empty.
     */
    public void stopFollowing(String followingUsername) {
        if (followingUsername == null || followingUsername.isEmpty()) {
            throw new IllegalArgumentException("Following username cannot be empty");
        }

        if (following.remove(followingUsername)) {
        }
    }

    /**
     * Returns true if the given username is in the list of users this User is
     * following,
     * false otherwise.
     * 
     * @param followingUsername The username to check if this User is following.
     * @return True if the given username is in the list of users this User is
     *         following,
     *         false otherwise.
     */
    public boolean isFollowing(String followingUsername) {
        return following.contains(followingUsername);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("User: %s\n", username));
        sb.append(String.format("Followers: %d\n", followers.size()));
        sb.append(String.format("Following: %d\n", following.size()));
        sb.append(String.format("Posts: %d\n", posts.size()));
        sb.append(String.format("Status: %s\n",
                loggedInto != null ? "Logged into " + loggedInto.getName() : "Not logged in"));

        if (!followers.isEmpty()) {
            sb.append("\nFollowers:\n");
            for (String follower : followers) {
                sb.append("  - ").append(follower).append("\n");
            }
        }

        if (!following.isEmpty()) {
            sb.append("\nFollowing:\n");
            for (String followed : following) {
                sb.append("  - ").append(followed).append("\n");
            }
        }

        if (!posts.isEmpty()) {
            sb.append("\nPosts:\n");
            for (Post post : posts) {
                sb.append("  ").append(post.toString().replace("\n", "\n  ")).append("\n");
            }
        }

        return sb.toString();
    }

    @Override
    public JPanel display() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // User info
        JLabel usernameLabel = new JLabel("Username: " + username);
        JLabel followersLabel = new JLabel("Followers: " + followers.size());
        JLabel followingLabel = new JLabel("Following: " + following.size());
        JLabel postsLabel = new JLabel("Posts: " + posts.size());
        JLabel statusLabel = new JLabel("Status: " +
                (loggedInto != null ? "Logged into " + loggedInto.getName() : "Not logged in"));

        panel.add(usernameLabel);
        panel.add(followersLabel);
        panel.add(followingLabel);
        panel.add(postsLabel);
        panel.add(statusLabel);

        // Add posts if any
        if (!posts.isEmpty()) {
            JLabel postsHeaderLabel = new JLabel("\nPosts:");
            panel.add(postsHeaderLabel);

            for (Post post : posts) {
                panel.add(post.display());
            }
        }

        return panel;
    }

    /**
     * Creates a new regular post using speech recognition.
     * 
     * @param audience           The visibility setting for the post
     * @param maxDurationSeconds Maximum recording duration in seconds
     * @return The newly created RegularPost
     * @throws Exception If there's an error during speech recognition
     */
    /*
     * Speech recognition feature disabled due to missing dependency.
     * 
     * public RegularPost createPostFromSpeech(PostAudience audience, int
     * maxDurationSeconds) throws Exception {
     * String text =
     * project.util.SpeechRecognizer.captureAndRecognize(maxDurationSeconds);
     * 
     * if (text == null || text.isEmpty()) {
     * throw new
     * IllegalArgumentException("Speech recognition failed or produced empty text");
     * }
     * 
     * 
     * RegularPost post = new RegularPost(username, text, audience);
     * this.owned.add(post);
     * this.posts.add(post);
     * 
     * // Log the post to the SocialConnect platform if logged in
     * if (loggedInto != null) {
     * loggedInto.logNewPost(post);
     * }
     * 
     * return post;
     * }
     */
}
