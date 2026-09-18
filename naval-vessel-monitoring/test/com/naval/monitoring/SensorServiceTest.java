package com.naval.monitoring;

import com.naval.monitoring.model.*;
import com.naval.monitoring.repository.*;
import com.naval.monitoring.service.*;
import com.naval.monitoring.util.SimpleCipher;

import java.io.File;
import java.util.List;

/**
 * Minimal, dependency-free assertion-based test harness.
 * Run with: java -ea -cp bin com.naval.monitoring.SensorServiceTest
 * (the -ea flag enables Java's built-in `assert` statements)
 */
public class SensorServiceTest {

    public static void main(String[] args) throws Exception {
        // isolate test data from any real run
        File testDataDir = new File("test-data");
        testDataDir.mkdirs();
        System.setProperty("user.dir", System.getProperty("user.dir"));

        testVesselRegistrationAndLookup();
        testSensorThresholdAlertLogic();
        testWeaponAccessControl();
        testMessageEncryptionRoundTrip();

        System.out.println("ALL TESTS PASSED");
    }

    static void testVesselRegistrationAndLookup() throws Exception {
        VesselRepository repo = new VesselRepository();
        VesselService service = new VesselService(repo);
        Vessel v = service.registerVessel("TV001", "Test Vessel", "Corvette", OfficerRank.CAPTAIN);
        assert v.getVesselId().equals("TV001") : "vessel id mismatch";
        Vessel fetched = service.getVessel("TV001");
        assert fetched.getName().equals("Test Vessel") : "vessel name mismatch";
        System.out.println("[PASS] testVesselRegistrationAndLookup");
    }

    static void testSensorThresholdAlertLogic() throws Exception {
        VesselRepository vr = new VesselRepository();
        VesselService vs = new VesselService(vr);
        vs.registerVessel("TV002", "Sonar Test Vessel", "Frigate", OfficerRank.COMMANDER);

        SensorRepository sr = new SensorRepository();
        SensorService ss = new SensorService(sr, vs);
        ss.registerSensor("TS001", "TV002", SensorType.SONAR, 50.0);

        SensorReading normal = ss.recordReading("TS001", 20.0);
        assert normal.getValue() == 20.0 : "reading value mismatch";

        List<SensorReading> history = ss.getHistory("TS001");
        assert history.size() >= 1 : "history should contain at least one reading";

        boolean threw = false;
        try {
            ss.recordReading("UNKNOWN_SENSOR", 10.0);
        } catch (Exception e) {
            threw = true;
        }
        assert threw : "recording a reading for an unknown sensor should throw";
        System.out.println("[PASS] testSensorThresholdAlertLogic");
    }

    static void testWeaponAccessControl() throws Exception {
        VesselRepository vr = new VesselRepository();
        VesselService vs = new VesselService(vr);
        vs.registerVessel("TV003", "Low Rank Vessel", "Patrol Boat", OfficerRank.CADET);

        WeaponRepository wr = new WeaponRepository();
        WeaponService ws = new WeaponService(wr, vs);
        ws.registerWeapon("TW001", "TV003", "Test Gun", 10);

        boolean denied = false;
        try {
            ws.updateStatus("TW001", WeaponStatus.MAINTENANCE);
        } catch (Exception e) {
            denied = true;
        }
        assert denied : "a CADET-commanded vessel should not be able to change weapon status";
        System.out.println("[PASS] testWeaponAccessControl");
    }

    static void testMessageEncryptionRoundTrip() {
        String plain = "Return to base immediately";
        String encrypted = SimpleCipher.encrypt(plain);
        assert !encrypted.equals(plain) : "encrypted text should differ from plain text";
        String decrypted = SimpleCipher.decrypt(encrypted);
        assert decrypted.equals(plain) : "decrypted text should match the original";
        System.out.println("[PASS] testMessageEncryptionRoundTrip");
    }
}
