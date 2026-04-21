package com.clinic.reminder;

import com.clinic.model.Appointment;

public interface ReminderService {
    void sendEmailReminder(Appointment appointment);
    void sendSMSReminder(Appointment appointment);
}
