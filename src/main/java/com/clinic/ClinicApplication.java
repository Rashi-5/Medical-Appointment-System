package com.clinic;

import com.clinic.decorator.ReminderDecorator;
import com.clinic.decorator.SearchDecorator;
import com.clinic.diary.Diary;
import com.clinic.factory.CSVDiaryFactory;
import com.clinic.factory.DiaryFactory;
import com.clinic.gui.ClinicGUI;
import com.clinic.model.Appointment;
import com.clinic.model.ReminderType;
import com.clinic.reminder.DefaultReminderService;

import javax.swing.*;
import java.util.List;

/**
 * Client class — matches the diagram exactly.
 * Owns the DiaryFactory and the Diary (decorated stack).
 * The GUI delegates all diary operations through this class.
 */
public class ClinicApplication {

    private DiaryFactory factory;
    private Diary diary;

    public ClinicApplication(DiaryFactory factory) {
        this.factory = factory;
        initialize();
    }

    /** Creates the diary via the factory and wraps it with decorators. */
    public void initialize() {
        Diary base = factory.createDiary();
        ReminderDecorator withReminders = new ReminderDecorator(base, new DefaultReminderService());
        diary = new SearchDecorator(withReminders);
    }

    /** Swap factory + reinitialise — used when the GUI changes storage type. */
    public void setFactory(DiaryFactory factory) {
        this.factory = factory;
        initialize();
    }

    public DiaryFactory getFactory() { return factory; }
    public Diary getDiary()          { return diary; }

    /** Books a new appointment through the diary. */
    public boolean bookAppointment(Appointment appointment) {
        return diary.addAppointment(appointment);
    }

    /** Cancels an existing appointment by ID. */
    public boolean cancelAppointment(String appointmentId) {
        return diary.deleteAppointment(appointmentId);
    }

    /** Returns all current appointments. */
    public List<Appointment> viewAppointments() {
        return diary.getAllAppointments();
    }

    /** Sends a reminder for the given appointment. */
    public void sendReminder(String appointmentId, ReminderType type) {
        diary.setReminder(appointmentId, type);
    }

    // -------------------------------------------------------------------------
    // Entry point
    // -------------------------------------------------------------------------

    public static void main(String[] args) {
        String base = System.getProperty("user.home") + "/clinic_data/";
        ClinicApplication app = new ClinicApplication(new CSVDiaryFactory(base + "appointments.csv"));

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new ClinicGUI(app).setVisible(true);
        });
    }
}
