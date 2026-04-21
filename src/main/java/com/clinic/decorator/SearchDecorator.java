package com.clinic.decorator;

import com.clinic.diary.Diary;
import com.clinic.model.Appointment;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Decorator that adds search functionality: find appointments by patient name
 * or doctor name using an in-memory index built from the current storage.
 */
public class SearchDecorator extends DiaryDecorator {

    // searchIndex: patientName.toLowerCase() -> set of appointment ids
    private final Map<String, Set<String>> searchIndex = new HashMap<>();

    public SearchDecorator(Diary diary) {
        super(diary);
        rebuildIndex();
    }

    @Override
    public boolean addAppointment(Appointment appointment) {
        boolean result = super.addAppointment(appointment);
        if (result) indexAppointment(appointment);
        return result;
    }

    @Override
    public boolean deleteAppointment(String appointmentId) {
        boolean result = super.deleteAppointment(appointmentId);
        if (result) removeFromIndex(appointmentId);
        return result;
    }

    /** Search appointments by patient name (case-insensitive, partial match). */
    public List<Appointment> searchByPatient(String query) {
        String q = query.toLowerCase().trim();
        return wrappedDiary.getAllAppointments().stream()
            .filter(a -> a.getPatientName().toLowerCase().contains(q))
            .collect(Collectors.toList());
    }

    /** Search appointments by doctor name (case-insensitive, partial match). */
    public List<Appointment> searchByDoctor(String query) {
        String q = query.toLowerCase().trim();
        return wrappedDiary.getAllAppointments().stream()
            .filter(a -> a.getDoctorName().toLowerCase().contains(q))
            .collect(Collectors.toList());
    }

    /** Rebuild the index from the underlying storage. */
    public void rebuildIndex() {
        searchIndex.clear();
        wrappedDiary.getAllAppointments().forEach(this::indexAppointment);
    }

    private void indexAppointment(Appointment a) {
        searchIndex.computeIfAbsent(a.getPatientName().toLowerCase(), k -> new HashSet<>()).add(a.getId());
    }

    private void removeFromIndex(String id) {
        searchIndex.values().forEach(ids -> ids.remove(id));
    }
}
