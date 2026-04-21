package com.clinic.gui;

import com.clinic.ClinicApplication;
import com.clinic.decorator.SearchDecorator;
import com.clinic.factory.*;
import com.clinic.model.Appointment;
import com.clinic.model.ReminderType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

/**
 * Swing GUI — delegates all diary operations to ClinicApplication,
 * which owns the factory and diary (as per the Q3 class diagram).
 */
public class ClinicGUI extends JFrame {

    private final ClinicApplication app;

    // --- Top panel ---
    private JComboBox<String> storageCombo;
    private JLabel activeStorageLabel;

    // --- Table ---
    private DefaultTableModel tableModel;
    private JTable table;

    // --- Input fields ---
    private JTextField patientField;
    private JTextField doctorField;
    private JTextField dateField;
    private JTextField timeField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField searchField;

    // --- Log ---
    private JTextArea logArea;

    public ClinicGUI(ClinicApplication app) {
        super("Medical Appointment System — Q3 Factory Method Demo");
        this.app = app;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 680);
        setLocationRelativeTo(null);
        buildUI();
        activeStorageLabel.setText("Active: CSV");
        refreshTable(app.viewAppointments());
    }

    // -------------------------------------------------------------------------
    // UI Construction
    // -------------------------------------------------------------------------

    private void buildUI() {
        setLayout(new BorderLayout(8, 8));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(buildTopPanel(),    BorderLayout.NORTH);
        add(buildCenterPanel(), BorderLayout.CENTER);
        add(buildLogPanel(),    BorderLayout.SOUTH);
    }

    private JPanel buildTopPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        p.setBorder(BorderFactory.createTitledBorder("Storage Selection (Factory Method Pattern)"));

        storageCombo = new JComboBox<>(new String[]{"CSV", "XML", "Database"});
        JButton applyBtn = new JButton("Apply Storage");
        activeStorageLabel = new JLabel("Active: —");
        activeStorageLabel.setFont(activeStorageLabel.getFont().deriveFont(Font.BOLD));

        applyBtn.addActionListener(e -> applyStorage((String) storageCombo.getSelectedItem()));

        p.add(new JLabel("Storage type:"));
        p.add(storageCombo);
        p.add(applyBtn);
        p.add(activeStorageLabel);
        return p;
    }

    private JSplitPane buildCenterPanel() {
        return new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildFormPanel(), buildTablePanel());
    }

    private JPanel buildFormPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder("Appointment Details"));
        p.setPreferredSize(new Dimension(290, 0));

        GridBagConstraints lc = new GridBagConstraints();
        lc.anchor = GridBagConstraints.WEST;
        lc.insets = new Insets(4, 6, 4, 4);

        GridBagConstraints fc = new GridBagConstraints();
        fc.fill = GridBagConstraints.HORIZONTAL;
        fc.weightx = 1.0;
        fc.insets = new Insets(4, 0, 4, 6);

        patientField = new JTextField(14);
        doctorField  = new JTextField(14);
        dateField    = new JTextField("2026-04-30", 14);
        timeField    = new JTextField("09:00", 14);
        emailField   = new JTextField(14);
        phoneField   = new JTextField(14);

        addRow(p, "Patient Name:",      patientField, lc, fc, 0);
        addRow(p, "Doctor Name:",       doctorField,  lc, fc, 1);
        addRow(p, "Date (yyyy-MM-dd):", dateField,    lc, fc, 2);
        addRow(p, "Time (HH:mm):",      timeField,    lc, fc, 3);
        addRow(p, "Email:",             emailField,   lc, fc, 4);
        addRow(p, "Phone:",             phoneField,   lc, fc, 5);

        JButton addBtn    = new JButton("Add Appointment");
        JButton deleteBtn = new JButton("Delete Selected");
        addBtn.setBackground(new Color(70, 130, 180));
        addBtn.setForeground(Color.BLUE);
        deleteBtn.setBackground(new Color(178, 34, 34));
        deleteBtn.setForeground(Color.RED);
        addBtn.addActionListener(e    -> addAppointment());
        deleteBtn.addActionListener(e -> deleteSelected());

        GridBagConstraints bc = new GridBagConstraints();
        bc.gridx = 0; bc.gridy = 6; bc.gridwidth = 2;
        bc.fill = GridBagConstraints.HORIZONTAL;
        bc.insets = new Insets(8, 6, 4, 6);
        p.add(addBtn, bc);
        bc.gridy = 7;
        bc.insets = new Insets(2, 6, 8, 6);
        p.add(deleteBtn, bc);

        // Reminder section
        JPanel reminderPanel = new JPanel(new GridBagLayout());
        reminderPanel.setBorder(BorderFactory.createTitledBorder("Send Reminder (Decorator)"));
        JComboBox<ReminderType> reminderCombo = new JComboBox<>(ReminderType.values());
        JButton reminderBtn = new JButton("Send Reminder");
        reminderBtn.addActionListener(e -> sendReminder((ReminderType) reminderCombo.getSelectedItem()));
        GridBagConstraints rc = new GridBagConstraints();
        rc.fill = GridBagConstraints.HORIZONTAL; rc.weightx = 1.0;
        rc.insets = new Insets(4, 4, 4, 4); rc.gridx = 0; rc.gridy = 0;
        reminderPanel.add(reminderCombo, rc);
        rc.gridy = 1;
        reminderPanel.add(reminderBtn, rc);

        // Search section
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search (SearchDecorator)"));
        searchField = new JTextField(12);
        JButton searchPatientBtn = new JButton("By Patient");
        JButton searchDoctorBtn  = new JButton("By Doctor");
        JButton showAllBtn       = new JButton("Show All");
        searchPatientBtn.addActionListener(e -> searchByPatient());
        searchDoctorBtn.addActionListener(e  -> searchByDoctor());
        showAllBtn.addActionListener(e       -> refreshTable(app.viewAppointments()));
        GridBagConstraints sc = new GridBagConstraints();
        sc.fill = GridBagConstraints.HORIZONTAL; sc.weightx = 1.0;
        sc.insets = new Insets(4, 4, 2, 4); sc.gridx = 0; sc.gridy = 0; sc.gridwidth = 2;
        searchPanel.add(searchField, sc);
        sc.gridy = 1; sc.gridwidth = 1;
        searchPanel.add(searchPatientBtn, sc);
        sc.gridx = 1;
        searchPanel.add(searchDoctorBtn, sc);
        sc.gridx = 0; sc.gridy = 2; sc.gridwidth = 2;
        searchPanel.add(showAllBtn, sc);

        GridBagConstraints sec = new GridBagConstraints();
        sec.gridx = 0; sec.gridwidth = 2; sec.fill = GridBagConstraints.HORIZONTAL;
        sec.weightx = 1.0; sec.insets = new Insets(6, 4, 4, 4);
        sec.gridy = 8;
        p.add(reminderPanel, sec);
        sec.gridy = 9;
        p.add(searchPanel, sec);

        GridBagConstraints filler = new GridBagConstraints();
        filler.gridx = 0; filler.gridy = 10; filler.weighty = 1.0;
        p.add(new JLabel(), filler);

        return p;
    }

    private JPanel buildTablePanel() {
        JPanel p = new JPanel(new BorderLayout(4, 4));
        p.setBorder(BorderFactory.createTitledBorder("Appointments"));

        tableModel = new DefaultTableModel(
            new String[]{"ID", "Patient", "Doctor", "Date", "Time"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(130);
        table.getColumnModel().getColumn(2).setPreferredWidth(130);

        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildLogPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder("System Log"));
        p.setPreferredSize(new Dimension(0, 110));

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        logArea.setBackground(new Color(30, 30, 30));
        logArea.setForeground(new Color(180, 255, 150));

        p.add(new JScrollPane(logArea), BorderLayout.CENTER);
        return p;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void addRow(JPanel p, String label, JComponent field,
                        GridBagConstraints lc, GridBagConstraints fc, int row) {
        lc.gridx = 0; lc.gridy = row;
        fc.gridx = 1; fc.gridy = row;
        p.add(new JLabel(label), lc);
        p.add(field, fc);
    }

    // -------------------------------------------------------------------------
    // Business Logic — all calls go through ClinicApplication
    // -------------------------------------------------------------------------

    private void applyStorage(String type) {
        String base = System.getProperty("user.home") + "/clinic_data/";
        DiaryFactory factory = switch (type) {
            case "XML"      -> new XMLDiaryFactory(base + "appointments.xml");
            case "Database" -> new DatabaseDiaryFactory("jdbc:sqlite:" + base + "appointments.db");
            default         -> new CSVDiaryFactory(base + "appointments.csv");
        };
        app.setFactory(factory);
        activeStorageLabel.setText("Active: " + type);
        log("Storage switched to " + type + " — ClinicApplication used factory to create new diary.");
        refreshTable(app.viewAppointments());
    }

    private void addAppointment() {
        String patient = patientField.getText().trim();
        String doctor  = doctorField.getText().trim();
        String dateStr = dateField.getText().trim();
        String timeStr = timeField.getText().trim();
        String email   = emailField.getText().trim();
        String phone   = phoneField.getText().trim();

        if (patient.isEmpty() || doctor.isEmpty()) {
            showError("Patient and Doctor names are required.");
            return;
        }
        try {
            LocalDate date = LocalDate.parse(dateStr);
            LocalTime time = LocalTime.parse(timeStr);

            boolean conflict = app.viewAppointments().stream()
                .anyMatch(a -> a.getDate().equals(date) && a.getTime().equals(time));
            if (conflict) {
                showError("Timeslot already booked: " + date + " at " + time);
                return;
            }

            String id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            Appointment a = new Appointment(id, patient, doctor, date, time);
            a.setEmail(email);
            a.setPhone(phone);
            app.bookAppointment(a);

            log("Added: " + a + (email.isEmpty() ? "" : " | email: " + email)
                              + (phone.isEmpty() ? "" : " | phone: " + phone));
            refreshTable(app.viewAppointments());
            patientField.setText("");
            doctorField.setText("");
            emailField.setText("");
            phoneField.setText("");
        } catch (DateTimeParseException ex) {
            showError("Invalid date/time format. Use yyyy-MM-dd and HH:mm.");
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { showError("Select an appointment to delete."); return; }
        String id = (String) tableModel.getValueAt(row, 0);
        app.cancelAppointment(id);
        log("Deleted appointment ID: " + id);
        refreshTable(app.viewAppointments());
    }

    private void sendReminder(ReminderType type) {
        int row = table.getSelectedRow();
        if (row < 0) { showError("Select an appointment first."); return; }
        String id = (String) tableModel.getValueAt(row, 0);
        Appointment a = app.getDiary().getAppointment(id);
        app.sendReminder(id, type);

        String contact = switch (type) {
            case EMAIL -> a != null && !a.getEmail().isBlank() ? a.getEmail() : "(no email on record)";
            case SMS, PUSH_NOTIFICATION -> a != null && !a.getPhone().isBlank() ? a.getPhone() : "(no phone on record)";
            case ALL -> {
                String e = a != null && !a.getEmail().isBlank() ? a.getEmail() : "(no email)";
                String ph = a != null && !a.getPhone().isBlank() ? a.getPhone() : "(no phone)";
                yield e + " & " + ph;
            }
        };
        log("[" + type + " REMINDER] → " + contact + " for appointment " + id);
    }

    private void searchByPatient() {
        String q = searchField.getText().trim();
        if (q.isEmpty()) { refreshTable(app.viewAppointments()); return; }
        SearchDecorator sd = (SearchDecorator) app.getDiary();
        List<Appointment> results = sd.searchByPatient(q);
        refreshTable(results);
        log("Patient search \"" + q + "\": " + results.size() + " result(s).");
    }

    private void searchByDoctor() {
        String q = searchField.getText().trim();
        if (q.isEmpty()) { refreshTable(app.viewAppointments()); return; }
        SearchDecorator sd = (SearchDecorator) app.getDiary();
        List<Appointment> results = sd.searchByDoctor(q);
        refreshTable(results);
        log("Doctor search \"" + q + "\": " + results.size() + " result(s).");
    }

    private void refreshTable(List<Appointment> appointments) {
        tableModel.setRowCount(0);
        for (Appointment a : appointments) {
            tableModel.addRow(new Object[]{
                a.getId(), a.getPatientName(), a.getDoctorName(), a.getDate(), a.getTime()
            });
        }
    }

    private void log(String msg) {
        logArea.append(msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
