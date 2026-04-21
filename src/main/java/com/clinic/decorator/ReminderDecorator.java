package com.clinic.decorator;

import com.clinic.diary.Diary;
import com.clinic.model.Appointment;
import com.clinic.model.ReminderType;
import com.clinic.reminder.ReminderService;

/**
 * Decorator that adds reminder functionality on top of the basic Diary.
 */
public class ReminderDecorator extends DiaryDecorator {

    private final ReminderService reminderService;

    public ReminderDecorator(Diary diary, ReminderService reminderService) {
        super(diary);
        this.reminderService = reminderService;
    }

    @Override
    public void setReminder(String appointmentId, ReminderType type) {
        Appointment a = wrappedDiary.getAppointment(appointmentId);
        if (a == null) {
            System.err.println("Reminder: appointment not found: " + appointmentId);
            return;
        }
        switch (type) {
            case EMAIL -> reminderService.sendEmailReminder(a);
            case SMS   -> reminderService.sendSMSReminder(a);
            case ALL   -> {
                reminderService.sendEmailReminder(a);
                reminderService.sendSMSReminder(a);
            }
            default    -> System.out.println("[PUSH] Reminder scheduled for: " + a);
        }
    }
}
