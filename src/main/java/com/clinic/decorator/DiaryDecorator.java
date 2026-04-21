package com.clinic.decorator;

import com.clinic.diary.Diary;
import com.clinic.model.Appointment;
import com.clinic.model.ReminderType;
import com.clinic.storage.DiaryImplementation;

import java.util.List;

/**
 * Abstract Decorator for Diary.
 * Wraps a Diary instance and forwards all calls, allowing subclasses
 * to extend behaviour without modifying the base Diary class.
 */
public abstract class DiaryDecorator extends Diary {

    protected final Diary wrappedDiary;

    protected DiaryDecorator(Diary diary) {
        super(diary.getImplementation());
        this.wrappedDiary = diary;
    }

    @Override
    public void setImplementation(DiaryImplementation impl) {
        wrappedDiary.setImplementation(impl);
        super.setImplementation(impl);
    }

    @Override
    public boolean addAppointment(Appointment appointment) {
        return wrappedDiary.addAppointment(appointment);
    }

    @Override
    public boolean deleteAppointment(String appointmentId) {
        return wrappedDiary.deleteAppointment(appointmentId);
    }

    @Override
    public Appointment getAppointment(String appointmentId) {
        return wrappedDiary.getAppointment(appointmentId);
    }

    @Override
    public List<Appointment> getAllAppointments() {
        return wrappedDiary.getAllAppointments();
    }

    @Override
    public void setReminder(String appointmentId, ReminderType type) {
        wrappedDiary.setReminder(appointmentId, type);
    }
}
