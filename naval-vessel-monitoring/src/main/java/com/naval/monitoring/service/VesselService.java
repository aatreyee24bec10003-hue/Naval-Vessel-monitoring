package com.naval.monitoring.service;

import com.naval.monitoring.exception.VesselNotFoundException;
import com.naval.monitoring.model.OfficerRank;
import com.naval.monitoring.model.Vessel;
import com.naval.monitoring.repository.VesselRepository;
import com.naval.monitoring.util.FileLogger;
import com.naval.monitoring.util.InputValidator;

import java.util.List;

/** Business logic for registering and looking up vessels (Vessel Registry module). */
public class VesselService {
    private final VesselRepository repository;

    public VesselService(VesselRepository repository) {
        this.repository = repository;
    }

    public Vessel registerVessel(String id, String name, String vesselClass, OfficerRank rank) {
        if (!InputValidator.isValidId(id)) {
            throw new IllegalArgumentException("Invalid vessel id: " + id);
        }
        if (InputValidator.isBlank(name)) {
            throw new IllegalArgumentException("Vessel name cannot be blank");
        }
        Vessel vessel = new Vessel(id, name, vesselClass, rank);
        repository.save(vessel);
        FileLogger.info("Vessel registered: " + vessel);
        return vessel;
    }

    public Vessel getVessel(String vesselId) throws VesselNotFoundException {
        return repository.findById(vesselId).orElseThrow(() -> new VesselNotFoundException(vesselId));
    }

    public List<Vessel> listVessels() {
        return repository.findAll();
    }

    public boolean vesselExists(String vesselId) {
        return repository.exists(vesselId);
    }
}
