package com.clinic.storage;

import com.clinic.model.Appointment;

/**
 * Bridge interface: separates the diary abstraction from its storage implementation.
 * Concrete implementations (CSV, XML, Database) plug in via this contract.
 */
public interface DiaryImplementation {
    boolean save(Appointment appointment);
    boolean delete(String appointmentId);
    Appointment find(String appointmentId);
    java.util.List<Appointment> findAll();
}
