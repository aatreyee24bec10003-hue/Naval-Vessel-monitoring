package com.naval.monitoring.service;

import com.naval.monitoring.exception.AccessDeniedException;
import com.naval.monitoring.exception.VesselNotFoundException;
import com.naval.monitoring.model.OfficerRank;
import com.naval.monitoring.model.Vessel;
import com.naval.monitoring.model.WeaponStatus;
import com.naval.monitoring.model.WeaponSystem;
import com.naval.monitoring.repository.WeaponRepository;
import com.naval.monitoring.util.FileLogger;
import com.naval.monitoring.util.InputValidator;

import java.util.List;

/**
 * Business logic for the Weapon System Readiness module. Status changes
 * require at least COMMANDER clearance on the owning vessel - a simple
 * demonstration of access control (security NFR).
 */
public class WeaponService {
    private final WeaponRepository weaponRepository;
    private final VesselService vesselService;

    public WeaponService(WeaponRepository weaponRepository, VesselService vesselService) {
        this.weaponRepository = weaponRepository;
        this.vesselService = vesselService;
    }

    public WeaponSystem registerWeapon(String weaponId, String vesselId, String name, int ammo)
            throws VesselNotFoundException {
        if (!InputValidator.isValidId(weaponId)) {
            throw new IllegalArgumentException("Invalid weapon id: " + weaponId);
        }
        vesselService.getVessel(vesselId);
        WeaponSystem weapon = new WeaponSystem(weaponId, vesselId, name, WeaponStatus.READY, ammo);
        weaponRepository.save(weapon);
        FileLogger.info("Weapon registered: " + weapon);
        return weapon;
    }

    public void updateStatus(String weaponId, WeaponStatus newStatus)
            throws VesselNotFoundException, AccessDeniedException {
        WeaponSystem weapon = weaponRepository.findById(weaponId)
                .orElseThrow(() -> new VesselNotFoundException("weapon:" + weaponId));
        Vessel vessel = vesselService.getVessel(weapon.getVesselId());
        if (vessel.getCommandingOfficerRank().ordinal() < OfficerRank.COMMANDER.ordinal()) {
            throw new AccessDeniedException("Status changes require COMMANDER clearance or higher on vessel "
                    + vessel.getVesselId());
        }
        weapon.setStatus(newStatus);
        weaponRepository.save(weapon);
        FileLogger.info("Weapon status updated: " + weapon);
    }

    public List<WeaponSystem> listWeaponsForVessel(String vesselId) {
        return weaponRepository.findByVessel(vesselId);
    }

    public List<WeaponSystem> listAllWeapons() {
        return weaponRepository.findAll();
    }

    /** Produces a simple readiness percentage across all registered weapon systems. */
    public double fleetReadinessPercentage() {
        List<WeaponSystem> all = weaponRepository.findAll();
        if (all.isEmpty()) return 0.0;
        long ready = all.stream().filter(w -> w.getStatus() == WeaponStatus.READY).count();
        return (ready * 100.0) / all.size();
    }
}
