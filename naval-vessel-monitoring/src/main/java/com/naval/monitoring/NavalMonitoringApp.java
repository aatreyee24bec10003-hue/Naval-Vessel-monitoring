package com.naval.monitoring;

import com.naval.monitoring.exception.*;
import com.naval.monitoring.model.*;
import com.naval.monitoring.repository.*;
import com.naval.monitoring.service.*;
import com.naval.monitoring.util.FileLogger;
import com.naval.monitoring.util.InputValidator;

import java.util.List;
import java.util.Scanner;

/**
 * Naval Vessel Monitoring &amp; Communication System (NVMCS).
 * Menu-driven command-line entry point tying together the Vessel Registry,
 * Sensor Data Management, Weapon System Readiness and Communication modules.
 */
public class NavalMonitoringApp {

    private final Scanner scanner = new Scanner(System.in);
    private final VesselService vesselService;
    private final SensorService sensorService;
    private final WeaponService weaponService;
    private final CommunicationService communicationService;

    public NavalMonitoringApp() {
        VesselRepository vesselRepository = new VesselRepository();
        SensorRepository sensorRepository = new SensorRepository();
        WeaponRepository weaponRepository = new WeaponRepository();
        MessageRepository messageRepository = new MessageRepository();

        this.vesselService = new VesselService(vesselRepository);
        this.sensorService = new SensorService(sensorRepository, vesselService);
        this.weaponService = new WeaponService(weaponRepository, vesselService);
        this.communicationService = new CommunicationService(messageRepository, vesselService);
    }

    public static void main(String[] args) {
        new java.io.File("data").mkdirs();
        FileLogger.info("NVMCS starting up");
        new NavalMonitoringApp().run();
        FileLogger.info("NVMCS shutting down");
    }

    private void run() {
        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1": vesselMenu(); break;
                    case "2": sensorMenu(); break;
                    case "3": weaponMenu(); break;
                    case "4": communicationMenu(); break;
                    case "0": running = false; break;
                    default: System.out.println("Invalid option. Please choose again.");
                }
            } catch (VesselNotFoundException | InvalidSensorDataException
                     | CommunicationException | AccessDeniedException e) {
                FileLogger.error(e.getMessage());
                System.out.println("Error: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input: " + e.getMessage());
            } catch (Exception e) {
                FileLogger.error("Unexpected error: " + e.getMessage());
                System.out.println("Unexpected error occurred; see log for details.");
            }
        }
        System.out.println("Goodbye.");
    }

    private void printMainMenu() {
        System.out.println();
        System.out.println("===== Naval Vessel Monitoring & Communication System =====");
        System.out.println("1. Vessel Registry");
        System.out.println("2. Sensor Data Management");
        System.out.println("3. Weapon System Readiness");
        System.out.println("4. Communication");
        System.out.println("0. Exit");
        System.out.print("Choose an option: ");
    }

    // ---------------------- Vessel Registry ----------------------

    private void vesselMenu() {
        System.out.println("-- Vessel Registry --");
        System.out.println("1. Register vessel  2. List vessels  0. Back");
        System.out.print("> ");
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                System.out.print("Vessel id: ");
                String id = scanner.nextLine().trim();
                System.out.print("Name: ");
                String name = scanner.nextLine().trim();
                System.out.print("Class (e.g. Destroyer, Frigate): ");
                String cls = scanner.nextLine().trim();
                OfficerRank rank = promptEnum("Commanding officer rank", OfficerRank.values());
                Vessel v = vesselService.registerVessel(id, name, cls, rank);
                System.out.println("Registered: " + v);
                break;
            case "2":
                List<Vessel> vessels = vesselService.listVessels();
                if (vessels.isEmpty()) System.out.println("No vessels registered.");
                vessels.forEach(System.out::println);
                break;
            default: break;
        }
    }

    // ---------------------- Sensor Data Management ----------------------

    private void sensorMenu() throws InvalidSensorDataException, VesselNotFoundException {
        System.out.println("-- Sensor Data Management --");
        System.out.println("1. Register sensor  2. Record reading  3. View history  4. List sensors  0. Back");
        System.out.print("> ");
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                System.out.print("Sensor id: ");
                String sensorId = scanner.nextLine().trim();
                System.out.print("Vessel id: ");
                String vesselId = scanner.nextLine().trim();
                SensorType type = promptEnum("Sensor type", SensorType.values());
                System.out.print("Warning threshold: ");
                double threshold = InputValidator.parseDoubleOrDefault(scanner.nextLine(), 100.0);
                Sensor s = sensorService.registerSensor(sensorId, vesselId, type, threshold);
                System.out.println("Registered: " + s);
                break;
            case "2":
                System.out.print("Sensor id: ");
                String sid = scanner.nextLine().trim();
                System.out.print("Reading value: ");
                double value = InputValidator.parseDoubleOrDefault(scanner.nextLine(), Double.NaN);
                SensorReading reading = sensorService.recordReading(sid, value);
                System.out.println("Recorded: " + reading);
                break;
            case "3":
                System.out.print("Sensor id: ");
                String histId = scanner.nextLine().trim();
                sensorService.getHistory(histId).forEach(System.out::println);
                break;
            case "4":
                sensorService.listAllSensors().forEach(System.out::println);
                break;
            default: break;
        }
    }

    // ---------------------- Weapon System Readiness ----------------------

    private void weaponMenu() throws VesselNotFoundException, AccessDeniedException {
        System.out.println("-- Weapon System Readiness --");
        System.out.println("1. Register weapon  2. Update status  3. List weapons  4. Fleet readiness %  0. Back");
        System.out.print("> ");
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                System.out.print("Weapon id: ");
                String wid = scanner.nextLine().trim();
                System.out.print("Vessel id: ");
                String vid = scanner.nextLine().trim();
                System.out.print("Weapon name: ");
                String wname = scanner.nextLine().trim();
                System.out.print("Ammunition count: ");
                int ammo = InputValidator.parseIntOrDefault(scanner.nextLine(), 0);
                WeaponSystem w = weaponService.registerWeapon(wid, vid, wname, ammo);
                System.out.println("Registered: " + w);
                break;
            case "2":
                System.out.print("Weapon id: ");
                String updateId = scanner.nextLine().trim();
                WeaponStatus status = promptEnum("New status", WeaponStatus.values());
                weaponService.updateStatus(updateId, status);
                System.out.println("Status updated.");
                break;
            case "3":
                weaponService.listAllWeapons().forEach(System.out::println);
                break;
            case "4":
                System.out.printf("Fleet readiness: %.1f%%%n", weaponService.fleetReadinessPercentage());
                break;
            default: break;
        }
    }

    // ---------------------- Communication ----------------------

    private void communicationMenu() throws VesselNotFoundException, CommunicationException {
        System.out.println("-- Communication --");
        System.out.println("1. Send message  2. View inbox  0. Back");
        System.out.print("> ");
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                System.out.print("From vessel id: ");
                String from = scanner.nextLine().trim();
                System.out.print("To vessel id: ");
                String to = scanner.nextLine().trim();
                System.out.print("Message body: ");
                String body = scanner.nextLine();
                MessagePriority priority = promptEnum("Priority", MessagePriority.values());
                communicationService.sendMessage(from, to, body, priority);
                System.out.println("Message sent (stored encrypted).");
                break;
            case "2":
                System.out.print("Vessel id: ");
                String vesselId = scanner.nextLine().trim();
                List<Message> inbox = communicationService.getInbox(vesselId);
                if (inbox.isEmpty()) System.out.println("No messages.");
                for (Message m : inbox) {
                    System.out.printf("[%s] from %s (%s): %s%n",
                            m.getSentAt(), m.getFromVesselId(), m.getPriority(),
                            communicationService.readMessage(m));
                }
                break;
            default: break;
        }
    }

    // ---------------------- helpers ----------------------

    private <T extends Enum<T>> T promptEnum(String label, T[] values) {
        while (true) {
            StringBuilder sb = new StringBuilder(label + " [");
            for (int i = 0; i < values.length; i++) {
                sb.append(i + 1).append("=").append(values[i]);
                if (i < values.length - 1) sb.append(", ");
            }
            sb.append("]: ");
            System.out.print(sb);
            String in = scanner.nextLine().trim();
            int idx = InputValidator.parseIntOrDefault(in, -1);
            if (idx >= 1 && idx <= values.length) return values[idx - 1];
            System.out.println("Invalid choice, try again.");
        }
    }
}
