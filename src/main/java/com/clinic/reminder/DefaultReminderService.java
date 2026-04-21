package com.clinic.reminder;

import com.clinic.model.Appointment;

public class DefaultReminderService implements ReminderService {

    @Override
    public void sendEmailReminder(Appointment a) {
        String to = a.getEmail().isBlank() ? "(no email on record)" : a.getEmail();
        System.out.printf("[EMAIL REMINDER] To: %s | Patient: %s | Dr.%s | %s at %s%n",
            to, a.getPatientName(), a.getDoctorName(), a.getDate(), a.getTime());
    }

    @Override
    public void sendSMSReminder(Appointment a) {
        String to = a.getPhone().isBlank() ? "(no phone on record)" : a.getPhone();
        System.out.printf("[SMS REMINDER] To: %s | Patient: %s | Dr.%s | %s at %s%n",
            to, a.getPatientName(), a.getDoctorName(), a.getDate(), a.getTime());
    }
}
