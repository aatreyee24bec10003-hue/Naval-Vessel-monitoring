package com.naval.monitoring.model;

/** A weapon system installed on a vessel and its current readiness state. */
public class WeaponSystem {
    private final String weaponId;
    private final String vesselId;
    private String name;
    private WeaponStatus status;
    private int ammunitionCount;

    public WeaponSystem(String weaponId, String vesselId, String name, WeaponStatus status, int ammunitionCount) {
        this.weaponId = weaponId;
        this.vesselId = vesselId;
        this.name = name;
        this.status = status;
        this.ammunitionCount = ammunitionCount;
    }

    public String getWeaponId() { return weaponId; }
    public String getVesselId() { return vesselId; }
    public String getName() { return name; }
    public WeaponStatus getStatus() { return status; }
    public void setStatus(WeaponStatus status) { this.status = status; }
    public int getAmmunitionCount() { return ammunitionCount; }
    public void setAmmunitionCount(int c) { this.ammunitionCount = c; }

    @Override
    public String toString() {
        return String.format("Weapon[id=%s, vessel=%s, name=%s, status=%s, ammo=%d]",
                weaponId, vesselId, name, status, ammunitionCount);
    }

    public String toCsv() {
        return String.join(",", weaponId, vesselId, name, status.name(), String.valueOf(ammunitionCount));
    }

    public static WeaponSystem fromCsv(String line) {
        String[] p = line.split(",", -1);
        return new WeaponSystem(p[0], p[1], p[2], WeaponStatus.valueOf(p[3]), Integer.parseInt(p[4]));
    }
}
