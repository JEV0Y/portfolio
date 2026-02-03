package project;

import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;

import project.enums.PostAudience;
import project.interfaces.Displayable;

/**
 * @author 
 * 
 */

/**
 * An abstract class representing a social media post in the SocialConnect
 * application.
 * This class serves as the base class for different types of posts (Regular and
 * Reshared).
 * It implements the Displayable interface for GUI representation.
 */
public abstract class Post implements Displayable {
    /** Counter to generate unique post IDs */
    private static int id_count = 0;

    /** Unique identifier for the post */
    protected int postID;

    /** Visibility setting for the post */
    protected PostAudience whoCanSee;

    /** Score indicating the post's popularity based on votes */
    protected int popularityScore;

    /** List of usernames who upvoted the post */
    protected ArrayList<String> upVoters;

    /** List of usernames who downvoted the post */
    protected ArrayList<String> downVoters;

    /** Username of the post owner */
    protected String owner;

    /**
     * Constructs a new Post with specified visibility and owner.
     * 
     * @param whoCanSee The visibility setting for the post
     * @param owner     The username of the post creator
     * @throws IllegalArgumentException if owner is null or empty
     */
    public Post(PostAudience whoCanSee, String owner) {
        if (owner == null || owner.isEmpty()) {
            throw new IllegalArgumentException("Post owner cannot be null or empty");
        }
        postID = ++id_count;
        this.whoCanSee = whoCanSee;
        this.owner = owner;
        this.popularityScore = 0;
        this.upVoters = new ArrayList<>();
        this.downVoters = new ArrayList<>();
    }

    /**
     * Returns the unique identifier of the post.
     * 
     * @return The post ID
     */
    public int getPostID() {
        return postID;
    }

    /**
     * Returns the visibility setting of the post.
     * 
     * @return The post audience
     */
    public PostAudience getAudience() {
        return whoCanSee;
    }

    /**
     * Returns the username of the post owner.
     * 
     * @return The post owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Returns the popularity score of the post based on votes.
     * 
     * @return The popularity score
     */
    public int getPopularityScore() {
        return popularityScore;
    }

    /**
     * Checks if a user can vote on this post.
     * Users cannot vote on their own posts or vote multiple times.
     *
     * @param username The username of the voter
     * @return true if the user can vote, false otherwise
     */
    public boolean canVote(String username) {
        // Users cannot vote on their own posts
        if (owner.equals(username)) {
            return false;
        }

        // Users cannot vote if they've already voted
        return !hasUpvoted(username) && !hasDownvoted(username);
    }

    /**
     * Adds an upvote to the post from the specified user.
     *
     * @param username The username of the voter
     * @return true if the vote was added, false if the user cannot vote
     */
    public boolean upvote(String username) {
        if (!canVote(username)) {
            return false;
        }

        upVoters.add(username);
        popularityScore++;
        return true;
    }

    /**
     * Adds a downvote to the post from the specified user.
     *
     * @param username The username of the voter
     * @return true if the vote was added, false if the user cannot vote
     */
    public boolean downvote(String username) {
        if (!canVote(username)) {
            return false;
        }

        downVoters.add(username);
        popularityScore--;
        return true;
    }

    /**
     * Checks if a user has upvoted this post.
     *
     * @param username The username to check
     * @return true if the user has upvoted, false otherwise
     */
    public boolean hasUpvoted(String username) {
        return upVoters.contains(username);
    }

    /**
     * Checks if a user has downvoted this post.
     *
     * @param username The username to check
     * @return true if the user has downvoted, false otherwise
     */
    public boolean hasDownvoted(String username) {
        return downVoters.contains(username);
    }

    /**
     * Checks if a user can view the post based on its visibility setting.
     * 
     * @param viewer The user who wants to view the post
     * @return True if the user can view, false otherwise
     */
    public boolean canView(User viewer) {
        switch (whoCanSee) {
            case Members:
                return true; // Everyone in the SocialConnect can view
            case Followers:
                return owner.equals(viewer.getUsername()) ||
                        viewer.isFollowing(owner); // Only owner and people following the owner can view
            default:
                return false;
        }
    }

    /**
     * Returns a GUI representation of the post.
     * 
     * @return A JPanel containing the post's details
     */
    @Override
    public JPanel display() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel postIDLabel = new JLabel("Post ID: " + postID);
        JLabel ownerLabel = new JLabel("Posted by: " + owner);
        JLabel audienceLabel = new JLabel("Visibility: " + whoCanSee.getDescription());
        JLabel scoreLabel = new JLabel("Score: " + popularityScore + " (" + upVoters.size() + " upvotes, "
                + downVoters.size() + " downvotes)");

        panel.add(postIDLabel);
        panel.add(ownerLabel);
        panel.add(audienceLabel);
        panel.add(scoreLabel);

        return panel;
    }

    /**
     * @param getVoteCount(
     * @return String
     */
    /**
     * @param getVoteCount(
     * @return String
     */
    /**
     * Returns the text content of the post.
     * 
     * @return The post's text content
     */
    public abstract String getText();

    /**
     * Returns the total vote count (upvotes minus downvotes).
     * 
     * @return The vote count
     */
    public int getVoteCount() {
        return upVoters.size() - downVoters.size();
    }

    /**
     * Checks if a user has voted on this post.
     * 
     * @param user     The user to check
     * @param isUpvote True to check for upvote, false for downvote
     * @return True if the user has voted as specified
     */
    public boolean hasVoted(User user, boolean isUpvote) {
        String username = user.getUsername();
        return isUpvote ? hasUpvoted(username) : hasDownvoted(username);
    }

    /**
     * Adds a vote to the post.
     * 
     * @param user     The user voting
     * @param isUpvote True for upvote, false for downvote
     * @return True if the vote was successful
     */
    public boolean vote(User user, boolean isUpvote) {
        String username = user.getUsername();
        return isUpvote ? upvote(username) : downvote(username);
    }

    /**
     * Returns a string representation of the post.
     * 
     * @return A string containing the post's details
     */
    public abstract String toString();
}
