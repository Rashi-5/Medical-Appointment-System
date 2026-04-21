package com.clinic.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment {
    private final String id;
    private final String patientName;
    private final String doctorName;
    private final LocalDate date;
    private final LocalTime time;
    private String notes;
    private String email;
    private String phone;

    public Appointment(String id, String patientName, String doctorName, LocalDate date, LocalTime time) {
        this.id = id;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.date = date;
        this.time = time;
        this.notes = "";
        this.email = "";
        this.phone = "";
    }

    public String getId()           { return id; }
    public String getPatientName()  { return patientName; }
    public String getDoctorName()   { return doctorName; }
    public LocalDate getDate()      { return date; }
    public LocalTime getTime()      { return time; }
    public String getNotes()        { return notes; }
    public void setNotes(String n)  { this.notes = n; }
    public String getEmail()        { return email; }
    public void setEmail(String e)  { this.email = e; }
    public String getPhone()        { return phone; }
    public void setPhone(String p)  { this.phone = p; }

    @Override
    public String toString() {
        return String.format("[%s] %s with Dr.%s on %s at %s", id, patientName, doctorName, date, time);
    }
}
