# AquaTrack — Purified Water Refilling Station Management System

PF201: OOP | BSIT-2A

Layered Java console app with SQLite (Maven).

## Run
```
mvn compile exec:java
```
`aquatrack.db` is created automatically on first run.

## Structure
- `dao` — DatabaseConnection (foreign keys ON), DatabaseInitializer (8 tables), UserDao
- `model` — User (record), Role, and domain classes
- `service` — RefillStationSystem (in-memory station data, to be moved to DAOs)
- `ui` — Authentication (register / login / role routing), Admin/Staff/Customer menus, StationConsole
- `Main` — entry point
