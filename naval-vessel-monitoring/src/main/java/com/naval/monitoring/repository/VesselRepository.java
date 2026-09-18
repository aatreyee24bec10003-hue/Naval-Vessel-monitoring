package com.naval.monitoring.repository;

import com.naval.monitoring.model.Vessel;
import com.naval.monitoring.util.DataDirectory;
import java.io.*;
import java.util.*;

/**
 * In-memory repository for vessels, backed by a CSV file for persistence
 * between runs. Uses a HashMap for O(1) lookup by vessel id (performance NFR).
 */
public class VesselRepository {
    private static final String FILE_PATH = "data/vessels.csv";
    private final Map<String, Vessel> store = new LinkedHashMap<>();

    public VesselRepository() {
        DataDirectory.ensureExists();
        load();
    }

    public void save(Vessel vessel) {
        store.put(vessel.getVesselId(), vessel);
        persist();
    }

    public Optional<Vessel> findById(String vesselId) {
        return Optional.ofNullable(store.get(vesselId));
    }

    public List<Vessel> findAll() {
        return new ArrayList<>(store.values());
    }

    public boolean exists(String vesselId) {
        return store.containsKey(vesselId);
    }

    private void persist() {
        try (PrintWriter out = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (Vessel v : store.values()) {
                out.println(v.toCsv());
            }
        } catch (IOException e) {
            System.err.println("Could not persist vessels: " + e.getMessage());
        }
    }

    private void load() {
        File f = new File(FILE_PATH);
        if (!f.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                Vessel v = Vessel.fromCsv(line);
                store.put(v.getVesselId(), v);
            }
        } catch (IOException e) {
            System.err.println("Could not load vessels: " + e.getMessage());
        }
    }
}
