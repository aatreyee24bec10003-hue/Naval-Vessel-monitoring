# Naval Vessel Monitoring & Communication System (NVMCS)

A layered, menu-driven **Java console application** for tracking naval
vessels, monitoring sensor readings, managing weapon-system readiness, and
exchanging encrypted inter-vessel messages. Built as the self-chosen
project for the *Programming in Java* (VITyarthi flipped course) submission.

> Educational/simulation project. Not a real command-and-control system.

## Overview
NVMCS ties together four functional modules around a shared `Vessel`
registry, using a classic **model / repository / service / presentation**
layering so each concern (data, persistence, business rules, and the CLI)
stays independently testable and swappable.

## Features
- **Vessel Registry** — register vessels with a name, class and
  commanding-officer rank; list all registered vessels.
- **Sensor Data Management** — register sensors (SONAR/RADAR/GPS/
  TEMPERATURE/PRESSURE/FUEL) per vessel, record timestamped readings, view
  a sensor's reading history, and automatically log a `WARN` alert when a
  reading exceeds the sensor's configured threshold.
- **Weapon System Readiness** — register weapon systems per vessel, update
  their status (READY / MAINTENANCE / DISABLED / RELOADING) — status
  changes require the vessel's commanding officer to hold **COMMANDER**
  rank or above — and view a fleet-wide readiness percentage.
- **Communication** — send inter-vessel messages with a priority
  (ROUTINE / PRIORITY / FLASH); message bodies are **encrypted before
  being written to disk** and only decrypted when explicitly read from an
  inbox.
- **Persistence** — all data is persisted to plain CSV files under `data/`
  so state survives between runs, with no external database required.
- **Logging** — every registration, reading, alert, status change and
  message send is appended to `data/system.log` with a timestamp.

## Technologies / Tools Used
- Java 21 (standard library only — no external dependencies)
- Plain CSV files for persistence (`data/*.csv`)
- Git for version control

## Project Structure
```
naval-vessel-monitoring/
├── src/main/java/com/naval/monitoring/
│   ├── model/        Vessel, Sensor, SensorReading, WeaponSystem, Message, enums
│   ├── repository/   VesselRepository, SensorRepository, WeaponRepository, MessageRepository
│   ├── service/       VesselService, SensorService, WeaponService, CommunicationService
│   ├── exception/     VesselNotFoundException, InvalidSensorDataException,
│   │                   CommunicationException, AccessDeniedException
│   ├── util/           FileLogger, SimpleCipher, InputValidator
│   └── NavalMonitoringApp.java   (CLI entry point)
├── test/               Manual assertion-based test harness (no external test framework)
├── docs/diagrams/      Architecture, workflow, use-case, class and sequence diagrams
├── statement.md        Problem statement, scope, target users, high-level features
└── README.md
```
That is 21 source files across 5 packages — well above the minimum 5–10
meaningful modules/classes requested for the submission.

## Steps to Install & Run
Requires **JDK 17 or later** on the PATH (developed and tested on JDK 21).

```bash
git clone <this-repository-url>
cd naval-vessel-monitoring

# Compile
mkdir -p bin
find src -name "*.java" > sources.txt
javac -d bin @sources.txt

# Run
java -cp bin com.naval.monitoring.NavalMonitoringApp
```
On first run the app creates a `data/` folder and CSV files automatically.
Use the numbered menu to navigate; `0` goes back / exits.

## Instructions for Testing
A lightweight, dependency-free test harness is included (no JUnit needed,
since this environment has no dependency manager configured):

```bash
javac -d bin @sources.txt
javac -d bin -cp bin test/com/naval/monitoring/SensorServiceTest.java
java -ea -cp bin com.naval.monitoring.SensorServiceTest
```
Run with `-ea` so the `assert` statements inside the test are active. All
assertions passing prints `ALL TESTS PASSED`.

You can also exercise the whole CLI end-to-end non-interactively:
```bash
java -cp bin com.naval.monitoring.NavalMonitoringApp < sample_session.txt
```

## Non-Functional Requirements Addressed
| NFR | How it's addressed |
|---|---|
| Performance | `HashMap`-backed repositories give O(1) lookup by id |
| Security | Message bodies encrypted at rest (`SimpleCipher`); weapon-status changes require COMMANDER+ rank; input validated before use |
| Usability | Numbered, self-explanatory menu-driven CLI with re-prompting on bad input |
| Reliability | Custom checked exceptions + try/catch at the CLI boundary so bad input never crashes the app |
| Scalability | New sensor types, vessels, weapons or vessels can be added without code changes; layers can be swapped independently |
| Maintainability | Layered architecture, Javadoc on every class, single-responsibility services |
| Logging / monitoring | Every action and alert is timestamped and appended to `data/system.log` |
| Resource efficiency | try-with-resources on every file stream; no unbounded in-memory growth beyond the session's data |

## Screenshots
See `docs/diagrams/` for architecture, workflow, use-case, class and
sequence diagrams illustrating the design (screenshots of a sample run are
included in the project report PDF).
