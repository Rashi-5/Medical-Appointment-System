package com.clinic.storage;

import com.clinic.model.Appointment;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/**
 * Stores appointments in a CSV file.
 * Format: id,patientName,doctorName,date,time,notes,email,phone
 */
public class CSVDiaryImplementation implements DiaryImplementation {

    private final String filePath;
    private static final String HEADER = "id,patientName,doctorName,date,time,notes,email,phone";

    public CSVDiaryImplementation(String filePath) {
        this.filePath = filePath;
        ensureFileExists();
    }

    private void ensureFileExists() {
        File f = new File(filePath);
        if (!f.exists()) {
            try {
                f.getParentFile().mkdirs();
                try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
                    pw.println(HEADER);
                }
            } catch (IOException e) {
                System.err.println("CSV init error: " + e.getMessage());
            }
        }
    }

    @Override
    public boolean save(Appointment a) {
        List<Appointment> all = findAll();
        // remove existing if updating
        all.removeIf(x -> x.getId().equals(a.getId()));
        all.add(a);
        return writeAll(all);
    }

    @Override
    public boolean delete(String id) {
        List<Appointment> all = findAll();
        boolean removed = all.removeIf(x -> x.getId().equals(id));
        if (removed) writeAll(all);
        return removed;
    }

    @Override
    public Appointment find(String id) {
        return findAll().stream().filter(a -> a.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public List<Appointment> findAll() {
        List<Appointment> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; } // skip header
                if (line.isBlank()) continue;
                String[] parts = line.split(",", 8);
                if (parts.length < 5) continue;
                Appointment a = new Appointment(
                    parts[0].trim(), parts[1].trim(), parts[2].trim(),
                    LocalDate.parse(parts[3].trim()), LocalTime.parse(parts[4].trim())
                );
                if (parts.length > 5) a.setNotes(parts[5].trim());
                if (parts.length > 6) a.setEmail(parts[6].trim());
                if (parts.length > 7) a.setPhone(parts[7].trim());
                list.add(a);
            }
        } catch (IOException e) {
            System.err.println("CSV read error: " + e.getMessage());
        }
        return list;
    }

    private boolean writeAll(List<Appointment> list) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            pw.println(HEADER);
            for (Appointment a : list) {
                pw.printf("%s,%s,%s,%s,%s,%s,%s,%s%n",
                    a.getId(), a.getPatientName(), a.getDoctorName(),
                    a.getDate(), a.getTime(), a.getNotes(), a.getEmail(), a.getPhone());
            }
            return true;
        } catch (IOException e) {
            System.err.println("CSV write error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public String toString() { return "CSV Storage (" + filePath + ")"; }
}
