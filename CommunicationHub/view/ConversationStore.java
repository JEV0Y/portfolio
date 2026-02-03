package view;

import model.User;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ConversationStore {
    private final User me;
    private final Map<String, List<String>> historyByContact = new HashMap<>();

    public ConversationStore(User me) {
        this.me = me;
        load();
    }

    private String keyOf(User contact) {
        return contact == null ? "" : contact.getPhoneNumber().toString();
    }

    private File storageFile() {
        String phone = (me != null ? me.getPhoneNumber().toString().replace("-", "") : "me");
        return new File("conversations_" + phone + ".txt");
    }

    private synchronized void load() {
        File f = storageFile();
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {
            String line;
            String currentKey = null;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#contact:")) {
                    currentKey = line.substring("#contact:".length()).trim();
                    historyByContact.putIfAbsent(currentKey, new ArrayList<>());
                } else if (currentKey != null) {
                    historyByContact.get(currentKey).add(line);
                }
            }
        } catch (IOException ignored) { }
    }

    private synchronized void save() {
        File f = storageFile();
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8))) {
            for (Map.Entry<String, List<String>> e : historyByContact.entrySet()) {
                pw.println("#contact:" + e.getKey());
                for (String line : e.getValue()) pw.println(line);
            }
        } catch (IOException ignored) { }
    }

    public synchronized List<String> getHistory(User contact) {
        return new ArrayList<>(historyByContact.getOrDefault(keyOf(contact), Collections.emptyList()));
    }

    public synchronized void appendOutgoing(User me, User contact, String text, String time) {
        String line = "> " + (me != null ? me.getFirstname() : "Me") + " (" + time + "): " + text;
        historyByContact.computeIfAbsent(keyOf(contact), k -> new ArrayList<>()).add(line);
        save();
    }

    public synchronized void appendIncoming(User contact, String text, String time) {
        String line = "< " + (contact != null ? contact.getFirstname() : "Contact") + " (" + time + "): " + text;
        historyByContact.computeIfAbsent(keyOf(contact), k -> new ArrayList<>()).add(line);
        save();
    }
}

