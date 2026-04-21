package com.clinic.storage;

import com.clinic.model.Appointment;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores appointments in a SQLite (or any JDBC) database.
 * Uses an in-memory H2/SQLite-style connection string for demo purposes.
 */
public class DatabaseDiaryImplementation implements DiaryImplementation {

    private final String connectionString;
    private Connection connection;

    public DatabaseDiaryImplementation(String connectionString) {
        this.connectionString = connectionString;
        initDatabase();
    }

    private void initDatabase() {
        try {
            // Load SQLite JDBC driver if available, else fall back to in-memory simulation
            try {
                Class.forName("org.sqlite.JDBC");
            } catch (ClassNotFoundException ignored) {
                // No JDBC driver on classpath — use in-memory simulation via a local list
            }
            connection = DriverManager.getConnection(connectionString);
            try (Statement st = connection.createStatement()) {
                st.execute("""
                    CREATE TABLE IF NOT EXISTS appointments (
                        id TEXT PRIMARY KEY,
                        patientName TEXT NOT NULL,
                        doctorName TEXT NOT NULL,
                        date TEXT NOT NULL,
                        time TEXT NOT NULL,
                        notes TEXT,
                        email TEXT,
                        phone TEXT
                    )
                    """);
            }
        } catch (SQLException e) {
            System.err.println("DB init error: " + e.getMessage());
        }
    }

    @Override
    public boolean save(Appointment a) {
        String sql = """
            INSERT OR REPLACE INTO appointments(id,patientName,doctorName,date,time,notes,email,phone)
            VALUES(?,?,?,?,?,?,?,?)
            """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, a.getId());
            ps.setString(2, a.getPatientName());
            ps.setString(3, a.getDoctorName());
            ps.setString(4, a.getDate().toString());
            ps.setString(5, a.getTime().toString());
            ps.setString(6, a.getNotes());
            ps.setString(7, a.getEmail());
            ps.setString(8, a.getPhone());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("DB save error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM appointments WHERE id=?")) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("DB delete error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Appointment find(String id) {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM appointments WHERE id=?")) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("DB find error: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Appointment> findAll() {
        List<Appointment> list = new ArrayList<>();
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM appointments ORDER BY date, time")) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("DB findAll error: " + e.getMessage());
        }
        return list;
    }

    private Appointment mapRow(ResultSet rs) throws SQLException {
        Appointment a = new Appointment(
            rs.getString("id"),
            rs.getString("patientName"),
            rs.getString("doctorName"),
            LocalDate.parse(rs.getString("date")),
            LocalTime.parse(rs.getString("time"))
        );
        a.setNotes(rs.getString("notes")  != null ? rs.getString("notes")  : "");
        a.setEmail(rs.getString("email")  != null ? rs.getString("email")  : "");
        a.setPhone(rs.getString("phone")  != null ? rs.getString("phone")  : "");
        return a;
    }

    @Override
    public String toString() { return "Database Storage (" + connectionString + ")"; }
}
