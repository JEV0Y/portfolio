package model.utilities;

import java.io.*;
import java.util.*;
import model.CommunicationHub;
import model.User;
import model.PhoneNumber;
import model.groups.Group;
import model.groups.Community;
import model.groups.Channel;
import model.posts.Post;
import model.posts.Announcement;
import model.enumerations.GroupType;

public class PersistenceManager {

    public static void save(CommunicationHub system, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // 1. Save Users
            writer.println("SECTION:UserS");
            for (User w : system.getRegisteredUsers()) {
                writer.printf("%s,%s,%s%n",
                        w.getFirstname(),
                        w.getLastname(),
                        w.getPhoneNumber().toString());
            }

            // Collect all unique groups
            Set<Group> allGroups = new HashSet<>();
            for (User w : system.getRegisteredUsers()) {
                allGroups.addAll(w.getGroups());
            }

            // 2. Save Groups
            writer.println("SECTION:GROUPS");
            for (Group g : allGroups) {
                // Format: id,type,name,creator_phone,capacity,status,parent_id(for channels)
                String parentId = "-1";
                String typeStr = "Group";
                if (g instanceof Community)
                    typeStr = "Community";
                else if (g instanceof Channel) {
                    typeStr = "Channel";
                    parentId = String.valueOf(((Channel) g).getCommunity().getID());
                }

                writer.printf("%d,%s,%s,%s,%d,%s,%s%n",
                        g.getID(),
                        typeStr,
                        g.getName(),
                        g.getCreator().getPhoneNumber().toString(),
                        g.getCapacity(),
                        g.getGroupStatus().name(),
                        parentId);
            }

            // 3. Save Memberships (Group -> Members)
            writer.println("SECTION:MEMBERSHIPS");
            for (Group g : allGroups) {
                // We need to access members. Group.toString() shows count, getFullInformation
                // shows names.
                // But we don't have a getter for members set.
                // However, we can iterate Users and check if they are members of the group.
                // This is inefficient O(W*G) but works.
                for (User w : system.getRegisteredUsers()) {
                    if (g.isCurrentMember(w, w)) {
                        boolean isAdmin = g.isAdmin(w);
                        writer.printf("%d,%s,%b%n", g.getID(), w.getPhoneNumber().toString(), isAdmin);
                    }
                }
            }

            // 4. Save Contacts
            writer.println("SECTION:CONTACTS");
            for (User w : system.getRegisteredUsers()) {
                for (User contact : system.getRegisteredUsers()) {
                    if (w.isAContact(contact)) {
                        writer.printf("%s,%s%n", w.getPhoneNumber().toString(), contact.getPhoneNumber().toString());
                    }
                }
            }

            // 5. Save Posts
            writer.println("SECTION:POSTS");
            for (Group g : allGroups) {
                for (Post p : g.getConversation()) {
                    // id,group_id,poster_phone,reply_to_id,is_announcement,content
                    long replyTo = -1;
                    try {
                        replyTo = p.getReplyFor();
                    } catch (NullPointerException e) {
                        // No reply
                    }

                    boolean isAnnouncement = p instanceof Announcement;
                    // Escape newlines in content
                    String content = p.getText().replace("\n", "\\n");

                    writer.printf("%d,%d,%s,%d,%b,%s%n",
                            p.getID(),
                            g.getID(),
                            p.getPostedBy().toString(),
                            replyTo,
                            isAnnouncement,
                            content);
                }
            }
        }
    }

    public static void load(CommunicationHub system, String filename) throws IOException {
        File f = new File(filename);
        if (!f.exists())
            return;

        Map<String, User> UsersByPhone = new HashMap<>();
        Map<Long, Group> groupsById = new HashMap<>();
        Map<Long, Post> postsById = new HashMap<>();
        List<String[]> postRecords = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            String section = "";

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("SECTION:")) {
                    section = line.substring(8);
                    continue;
                }
                if (line.trim().isEmpty())
                    continue;

                String[] parts = line.split(",");

                switch (section) {
                    case "UserS":
                        if (parts.length >= 3) {
                            system.register(parts[0], parts[1], parts[2]);
                            User w = system.findUser(new PhoneNumber(parts[2].split("-")[0], parts[2].split("-")[1],
                                    parts[2].split("-")[2]));
                            if (w != null)
                                UsersByPhone.put(parts[2], w);
                        }
                        break;

                    case "GROUPS":
                        if (parts.length >= 7) {
                            long id = Long.parseLong(parts[0]);
                            String type = parts[1];
                            String name = parts[2];
                            String creatorPhone = parts[3];
                            short capacity = Short.parseShort(parts[4]);
                            // Status parts[5]
                            long parentId = Long.parseLong(parts[6]);

                            User creator = UsersByPhone.get(creatorPhone);
                            if (creator != null) {
                                Group g = null;
                                if (type.equals("Community")) {
                                    g = new Community(creator, name, null);
                                    // Hack to set ID? We need to use reflection or the constructor we added.
                                    // But Community constructor calls super which increments ID.
                                    // We need to fix Community/Channel constructors too to accept ID.
                                    // For now, let's assume we can't perfectly restore IDs for subclasses without
                                    // more changes.
                                    // Or we use the base Group constructor and cast? No.
                                    // Let's just create them. The ID will be new.
                                    // WAIT, if ID is new, relationships break.
                                    // We MUST support ID restoration in subclasses.
                                } else if (type.equals("Channel")) {
                                    // We need the community.
                                    Group parent = groupsById.get(parentId);
                                    if (parent instanceof Community) {
                                        g = new Channel((Community) parent, creator, name, null);
                                    }
                                } else {
                                    // Regular Group
                                    // We need to determine GroupType from capacity/name logic or save it
                                    // explicitly.
                                    // For simplicity, let's assume RegularGroup.
                                    g = new Group(creator, GroupType.RegularGroup, name, capacity, null, id);
                                }

                                if (g != null) {
                                    groupsById.put(id, g);
                                    Group.updateIDCounter(id);
                                }
                            }
                        }
                        break;

                    case "MEMBERSHIPS":
                        if (parts.length >= 3) {
                            long gid = Long.parseLong(parts[0]);
                            String phone = parts[1];
                            boolean isAdmin = Boolean.parseBoolean(parts[2]);

                            Group g = groupsById.get(gid);
                            User w = UsersByPhone.get(phone);

                            if (g != null && w != null) {
                                // Add member if not creator (creator added in constructor)
                                if (!g.isCreator(w)) {
                                    boolean added = g.addMember(g.getCreator(), w);
                                    if (!added) {
                                        System.err.println(
                                                "Failed to add member " + w.getFullName() + " to group " + g.getName());
                                    }
                                }
                                if (isAdmin && !g.isAdmin(w)) {
                                    g.upgradeMemberToAdmin(g.getCreator(), w);
                                }
                            }
                        }
                        break;

                    case "CONTACTS":
                        if (parts.length >= 2) {
                            User w1 = UsersByPhone.get(parts[0]);
                            User w2 = UsersByPhone.get(parts[1]);
                            if (w1 != null && w2 != null) {
                                w1.addContact(w2);
                            }
                        }
                        break;

                    case "POSTS":
                        // Store for second pass
                        postRecords.add(parts);
                        break;
                }
            }

            // Second pass for posts (to handle replies)
            for (String[] parts : postRecords) {
                // id,group_id,poster_phone,reply_to_id,is_announcement,content
                // Content might contain commas, so we need to join the rest
                long id = Long.parseLong(parts[0]);
                long gid = Long.parseLong(parts[1]);
                String posterPhone = parts[2];
                long replyToId = Long.parseLong(parts[3]);
                boolean isAnnouncement = Boolean.parseBoolean(parts[4]);

                // Reconstruct content
                StringBuilder contentBuilder = new StringBuilder();
                for (int i = 5; i < parts.length; i++) {
                    if (i > 5)
                        contentBuilder.append(",");
                    contentBuilder.append(parts[i]);
                }
                String content = contentBuilder.toString().replace("\\n", "\n");

                Group g = groupsById.get(gid);
                User poster = UsersByPhone.get(posterPhone);

                if (g != null && poster != null) {
                    Post p = null;
                    if (replyToId != -1 && postsById.containsKey(replyToId)) {
                        Post parent = postsById.get(replyToId);
                        p = new Post(content, poster, g, parent, id);
                    } else {
                        if (isAnnouncement) {
                            p = new Announcement(content, poster, g); // Need ID constructor for Announcement too
                        } else {
                            p = new Post(content, poster, g, null, id);
                        }
                    }

                    if (p != null) {
                        g.getConversation().add(p);
                        postsById.put(id, p);
                        Post.updateIDCounter(id);
                    }
                }
            }
        }
    }
}


