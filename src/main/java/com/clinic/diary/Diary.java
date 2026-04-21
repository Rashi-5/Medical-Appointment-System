package com.clinic.diary;

import com.clinic.model.Appointment;
import com.clinic.model.ReminderType;
import com.clinic.storage.DiaryImplementation;

import java.util.List;

/**
 * Diary abstraction (Bridge pattern).
 * Holds a reference to a DiaryImplementation and delegates persistence to it.
 * Decorators extend this class to add enhanced behaviour.
 */
public class Diary {

    private DiaryImplementation impl;

    public Diary(DiaryImplementation impl) {
        this.impl = impl;
    }

    /** Swap storage implementation at runtime — Bridge pattern flexibility. */
    public void setImplementation(DiaryImplementation impl) {
        this.impl = impl;
    }

    public DiaryImplementation getImplementation() {
        return impl;
    }

    public boolean addAppointment(Appointment appointment) {
        return impl.save(appointment);
    }

    public boolean deleteAppointment(String appointmentId) {
        return impl.delete(appointmentId);
    }

    public Appointment getAppointment(String appointmentId) {
        return impl.find(appointmentId);
    }

    public List<Appointment> getAllAppointments() {
        return impl.findAll();
    }

    /** Hook for Decorator subclasses; base implementation is a no-op. */
    public void setReminder(String appointmentId, ReminderType type) {
        // overridden by ReminderDecorator
    }
}
