# Medical Appointment System
**7SENG004C Coursework 2 — Question 3**
University of Westminster | Object-Oriented Modelling and Implementation

---

## Overview

A Java application demonstrating three Gang-of-Four design patterns applied to a medical clinic appointment diary:

| Pattern | Role in this system |
|---|---|
| **Factory Method** | Hides which storage implementation (CSV / XML / Database) is used from the client |
| **Bridge** | Decouples the `Diary` abstraction from its `DiaryImplementation`, allowing storage to be swapped at runtime |
| **Decorator** | Extends diary behaviour with `ReminderDecorator` (SMS/email reminders) and `SearchDecorator` (patient/doctor search) without modifying the base class |

---

## Requirements

- Java 17 or later
- No Maven or external tools needed — everything is handled by `run.sh`

Check your Java version:
```bash
java -version
```

---

## Running the Application

```bash
cd /Users/rashi/IIT/ASD/Medical-Appointment-System
./run.sh
```

`run.sh` compiles all sources, packages a fat JAR (including the SQLite driver), and launches the Swing GUI.

To run the already-built JAR directly (no recompile):
```bash
java -jar MedicalAppointmentSystem.jar
```

---

## Project Structure

```
src/main/java/com/clinic/
├── ClinicApplication.java          ← Entry point (main)
├── model/
│   ├── Appointment.java            ← Domain object (id, patient, doctor, date, time)
│   └── ReminderType.java           ← Enum: EMAIL, SMS, PUSH_NOTIFICATION, ALL
├── storage/
│   ├── DiaryImplementation.java    ← Bridge interface (save / delete / find / findAll)
│   ├── CSVDiaryImplementation.java ← Stores appointments in a .csv file
│   ├── XMLDiaryImplementation.java ← Stores appointments in a .xml file
│   └── DatabaseDiaryImplementation.java ← Stores appointments in SQLite via JDBC
├── diary/
│   └── Diary.java                  ← Bridge abstraction — delegates to DiaryImplementation
├── factory/
│   ├── DiaryFactory.java           ← Abstract factory declaring createDiary()
│   ├── CSVDiaryFactory.java
│   ├── XMLDiaryFactory.java
│   └── DatabaseDiaryFactory.java
├── decorator/
│   ├── DiaryDecorator.java         ← Abstract decorator wrapping a Diary
│   ├── ReminderDecorator.java      ← Adds reminder dispatch on setReminder()
│   └── SearchDecorator.java        ← Adds searchByPatient() / searchByDoctor()
├── reminder/
│   ├── ReminderService.java        ← Interface: sendEmailReminder / sendSMSReminder
│   └── DefaultReminderService.java ← Simulated implementation (prints to console)
└── gui/
    └── ClinicGUI.java              ← Swing GUI
```

---

## GUI Features

- **Storage Selection** — switch between CSV, XML, or Database from the dropdown; the factory silently creates the correct implementation
- **Add Appointment** — enter patient name, doctor name, date (`yyyy-MM-dd`), and time (`HH:mm`); duplicate timeslots are rejected
- **Delete Appointment** — select a row in the table and click Delete
- **Send Reminder** — select an appointment and a reminder type (EMAIL / SMS / PUSH / ALL); output appears in the system log and console
- **Search** — search appointments by patient name or doctor name (case-insensitive, partial match)
- **System Log** — bottom panel shows all actions performed during the session

---

## Data Storage

Appointment data is saved to `~/clinic_data/`:

| Storage type | File |
|---|---|
| CSV | `~/clinic_data/appointments.csv` |
| XML | `~/clinic_data/appointments.xml` |
| Database | `~/clinic_data/appointments.db` (SQLite) |

Data persists between runs. Switching storage type loads appointments from that store independently.

---

## Design Pattern Details

### Factory Method
`DiaryFactory` declares the abstract `createDiary()` method. Each concrete factory (`CSVDiaryFactory`, `XMLDiaryFactory`, `DatabaseDiaryFactory`) overrides it to instantiate the correct `DiaryImplementation`. The `ClinicApplication` and GUI only ever hold a `DiaryFactory` reference — the storage type is completely hidden.

### Bridge
`Diary` holds a `DiaryImplementation` reference. The abstraction and implementation vary independently: new storage backends can be added without touching `Diary`, and new diary abstractions can be added without touching any storage class. `setImplementation()` allows hot-swapping storage at runtime.

### Decorator
`DiaryDecorator` wraps a `Diary` and forwards all calls, allowing transparent stacking:
```
DatabaseDiary  ←wrapped by→  ReminderDecorator  ←wrapped by→  SearchDecorator
```
The client holds the outermost decorator but interacts with the standard `Diary` interface throughout.
