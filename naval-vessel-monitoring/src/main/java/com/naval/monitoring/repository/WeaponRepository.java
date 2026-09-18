package com.naval.monitoring.repository;

import com.naval.monitoring.model.WeaponSystem;
import com.naval.monitoring.util.DataDirectory;
import java.io.*;
import java.util.*;

/** Repository for weapon systems, persisted as a CSV file. */
public class WeaponRepository {
    private static final String FILE_PATH = "data/weapons.csv";
    private final Map<String, WeaponSystem> store = new LinkedHashMap<>();

    public WeaponRepository() { DataDirectory.ensureExists();
        load(); }

    public void save(WeaponSystem weapon) {
        store.put(weapon.getWeaponId(), weapon);
        persist();
    }

    public Optional<WeaponSystem> findById(String weaponId) {
        return Optional.ofNullable(store.get(weaponId));
    }

    public List<WeaponSystem> findByVessel(String vesselId) {
        List<WeaponSystem> result = new ArrayList<>();
        for (WeaponSystem w : store.values()) {
            if (w.getVesselId().equals(vesselId)) result.add(w);
        }
        return result;
    }

    public List<WeaponSystem> findAll() { return new ArrayList<>(store.values()); }

    private void persist() {
        try (PrintWriter out = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (WeaponSystem w : store.values()) out.println(w.toCsv());
        } catch (IOException e) {
            System.err.println("Could not persist weapons: " + e.getMessage());
        }
    }

    private void load() {
        File f = new File(FILE_PATH);
        if (!f.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                WeaponSystem w = WeaponSystem.fromCsv(line);
                store.put(w.getWeaponId(), w);
            }
        } catch (IOException e) {
            System.err.println("Could not load weapons: " + e.getMessage());
        }
    }
}
