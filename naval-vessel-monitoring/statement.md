# Problem Statement

## Problem Statement
Naval vessels operate as part of a coordinated fleet and depend on three
things working correctly at all times: onboard sensors that report the
physical state of the ship and its environment, weapon systems that must be
tracked for operational readiness, and reliable inter-vessel communication
so that commands and situation reports reach the right ship securely. In
many training and simulation contexts these three concerns are handled by
separate, ad-hoc spreadsheets or scripts, which makes it hard to see a
consistent, auditable picture of a vessel's state and easy to leak
communication content in plain text.

The **Naval Vessel Monitoring & Communication System (NVMCS)** is a
Java console application that brings vessel registration, sensor
monitoring, weapon-system readiness tracking, and encrypted inter-vessel
messaging together under one consistent, layered, auditable system.

## Scope of the Project
- Registering vessels and their commanding officer's rank.
- Registering sensors (SONAR, RADAR, GPS, TEMPERATURE, PRESSURE, FUEL) per
  vessel, recording readings, and raising threshold alerts.
- Registering weapon systems per vessel and updating their readiness
  status, gated by a simple rank-based access control check.
- Sending and reading inter-vessel messages, stored encrypted at rest.
- Persisting all data to CSV files and logging every significant action
  and alert to a timestamped log file.
- The system is a single-user, offline command-line tool intended for a
  training/simulation/coursework context; it is **not** a production
  military system.

## Target Users
- Students and evaluators using this as a coursework demonstration of
  layered Java application design.
- A naval training exercise operator who wants a lightweight, offline
  console tool to simulate fleet monitoring and communication.

## High-Level Features
1. **Vessel Registry** — register and list vessels with a commanding
   officer rank.
2. **Sensor Data Management** — register sensors, record readings, view
   history, and receive threshold-based alerts.
3. **Weapon System Readiness** — register weapon systems, update status
   (rank-gated), list systems, view fleet-wide readiness percentage.
4. **Communication** — send and read encrypted inter-vessel messages with
   a priority level (ROUTINE / PRIORITY / FLASH).
