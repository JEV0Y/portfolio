package view;

import model.User;
import model.groups.Group;

import java.util.ArrayList;
import java.util.List;

public class GroupsStore {
    private final User owner;
    private final List<Group> groups = new ArrayList<>();

    public GroupsStore(User owner) {
        this.owner = owner;
        if (owner != null) {
            this.groups.addAll(owner.getGroups());
        }
    }

    public synchronized void add(Group g) {
        if (g != null)
            groups.add(g);
    }

    public synchronized List<Group> all() {
        return new ArrayList<>(groups);
    }

    public User getOwner() {
        return owner;
    }
}

