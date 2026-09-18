package com.naval.monitoring.repository;

import com.naval.monitoring.model.Message;
import com.naval.monitoring.util.DataDirectory;
import java.io.*;
import java.util.*;

/** Repository for communication messages (stored encrypted), persisted as CSV. */
public class MessageRepository {
    private static final String FILE_PATH = "data/messages.csv";
    private final List<Message> store = new ArrayList<>();

    public MessageRepository() { DataDirectory.ensureExists();
        load(); }

    public void save(Message message) {
        store.add(message);
        try (PrintWriter out = new PrintWriter(new FileWriter(FILE_PATH, true))) {
            out.println(message.toCsv());
        } catch (IOException e) {
            System.err.println("Could not persist message: " + e.getMessage());
        }
    }

    public List<Message> findByRecipient(String vesselId) {
        List<Message> result = new ArrayList<>();
        for (Message m : store) {
            if (m.getToVesselId().equals(vesselId)) result.add(m);
        }
        return result;
    }

    public List<Message> findAll() { return new ArrayList<>(store); }

    private void load() {
        File f = new File(FILE_PATH);
        if (!f.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                store.add(Message.fromCsv(line));
            }
        } catch (IOException e) {
            System.err.println("Could not load messages: " + e.getMessage());
        }
    }
}
