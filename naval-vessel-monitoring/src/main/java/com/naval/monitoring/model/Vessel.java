package com.naval.monitoring.model;

/**
 * Represents a naval vessel registered in the fleet.
 * Acts as the aggregate root that sensors, weapon systems and
 * communication messages are associated with (by vessel id).
 */
public class Vessel {
    private final String vesselId;
    private String name;
    private String vesselClass;
    private OfficerRank commandingOfficerRank;

    public Vessel(String vesselId, String name, String vesselClass, OfficerRank commandingOfficerRank) {
        this.vesselId = vesselId;
        this.name = name;
        this.vesselClass = vesselClass;
        this.commandingOfficerRank = commandingOfficerRank;
    }

    public String getVesselId() { return vesselId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getVesselClass() { return vesselClass; }
    public void setVesselClass(String vesselClass) { this.vesselClass = vesselClass; }
    public OfficerRank getCommandingOfficerRank() { return commandingOfficerRank; }
    public void setCommandingOfficerRank(OfficerRank rank) { this.commandingOfficerRank = rank; }

    @Override
    public String toString() {
        return String.format("Vessel[id=%s, name=%s, class=%s, CO-rank=%s]",
                vesselId, name, vesselClass, commandingOfficerRank);
    }

    /** Serializes this vessel to a single CSV line for file persistence. */
    public String toCsv() {
        return String.join(",", vesselId, name, vesselClass, commandingOfficerRank.name());
    }

    public static Vessel fromCsv(String line) {
        String[] p = line.split(",", -1);
        return new Vessel(p[0], p[1], p[2], OfficerRank.valueOf(p[3]));
    }
}
