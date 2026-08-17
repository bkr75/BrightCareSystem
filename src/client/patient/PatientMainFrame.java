package client.patient;

import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Date;
import java.util.List;

import javax.swing.*;

import model.Appointment;
import model.DoctorSchedule;
import model.Patient;
import rmi.ClinicRemote;
import shared.Operation;
import shared.Request;
import shared.Response;

/**
 * GUI entry point for the Patient role (single-file version, same
 * idea as client.doctor.DoctorMainFrame). Covers the same 5 features
 * as the CLI's PatientClient: Book Appointment, Cancel Appointment,
 * View Appointment History, Update Personal Information, Check
 * Doctor Availability.
 *
 * The original CLI class, PatientClient, is left untouched — this
 * is an additional GUI entry point, not a replacement.
 */
public class PatientMainFrame extends JFrame {

    private ClinicRemote clinic;

    public PatientMainFrame() {
        super("BrightCare - Patient Module");
        connectToServer();

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Book Appointment", buildBookAppointmentTab());
        tabbedPane.addTab("Cancel Appointment", buildCancelAppointmentTab());
        tabbedPane.addTab("Appointment History", buildViewHistoryTab());
        tabbedPane.addTab("Update Info", buildUpdateInfoTab());
        tabbedPane.addTab("Doctor Availability", buildCheckAvailabilityTab());

        setContentPane(tabbedPane);
        setSize(750, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void connectToServer() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            clinic = (ClinicRemote) registry.lookup("ClinicService");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Could not connect to BrightCare Clinic Server: " + e.getMessage(),
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------- Tab 1: Book Appointment ----------

    private JPanel buildBookAppointmentTab() {
        JTextField patientIdField = new JTextField(15);
        JTextField doctorIdField = new JTextField(15);
        JTextField scheduleIdField = new JTextField(15);
        JTextField dateField = new JTextField(15);
        JLabel statusLabel = new JLabel(" ");
        JButton bookButton = new JButton("Book Appointment");

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = defaultGbc();
        addFormRow(formPanel, gbc, 0, "Patient ID:", patientIdField);
        addFormRow(formPanel, gbc, 1, "Doctor ID:", doctorIdField);
        addFormRow(formPanel, gbc, 2, "Schedule ID:", scheduleIdField);
        addFormRow(formPanel, gbc, 3, "Appointment Date (YYYY-MM-DD):", dateField);

        bookButton.addActionListener(e -> {
            try {
                int patientId = Integer.parseInt(patientIdField.getText().trim());
                int doctorId = Integer.parseInt(doctorIdField.getText().trim());
                int scheduleId = Integer.parseInt(scheduleIdField.getText().trim());
                Date appointmentDate = Date.valueOf(dateField.getText().trim());

                Appointment appointment = new Appointment(
                        patientId, doctorId, scheduleId, appointmentDate, "BOOKED");

                Request request = new Request(Operation.BOOK_APPOINTMENT, appointment, "patient");
                Response response = clinic.processRequest(request);
                setStatus(statusLabel, response);
            } catch (Exception ex) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Error booking appointment: " + ex.getMessage());
            }
        });

        return wrapTab(formPanel, bookButton, statusLabel);
    }

    // ---------- Tab 2: Cancel Appointment ----------

    private JPanel buildCancelAppointmentTab() {
        JTextField appointmentIdField = new JTextField(15);
        JLabel statusLabel = new JLabel(" ");
        JButton cancelButton = new JButton("Cancel Appointment");

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = defaultGbc();
        addFormRow(formPanel, gbc, 0, "Appointment ID:", appointmentIdField);

        cancelButton.addActionListener(e -> {
            try {
                int appointmentId = Integer.parseInt(appointmentIdField.getText().trim());

                Request request = new Request(Operation.CANCEL_APPOINTMENT, appointmentId, "patient");
                Response response = clinic.processRequest(request);
                setStatus(statusLabel, response);
            } catch (Exception ex) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Error cancelling appointment: " + ex.getMessage());
            }
        });

        return wrapTab(formPanel, cancelButton, statusLabel);
    }

    // ---------- Tab 3: View Appointment History ----------

    @SuppressWarnings("unchecked")
    private JPanel buildViewHistoryTab() {
        JTextField patientIdField = new JTextField(15);
        JLabel statusLabel = new JLabel(" ");
        JButton viewButton = new JButton("View History");
        JTextArea resultArea = new JTextArea(12, 50);
        resultArea.setEditable(false);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = defaultGbc();
        addFormRow(formPanel, gbc, 0, "Patient ID:", patientIdField);

        viewButton.addActionListener(e -> {
            resultArea.setText("");
            try {
                int patientId = Integer.parseInt(patientIdField.getText().trim());

                Request request = new Request(Operation.VIEW_APPOINTMENT_HISTORY, patientId, "patient");
                Response response = clinic.processRequest(request);
                setStatus(statusLabel, response);

                if (response.isSuccess() && response.getData() instanceof List) {
                    List<Appointment> appointments = (List<Appointment>) response.getData();

                    if (appointments.isEmpty()) {
                        resultArea.append("No appointments found.\n");
                    }

                    for (Appointment a : appointments) {
                        resultArea.append(
                                "Appointment #" + a.getAppointmentId()
                                        + " | Doctor: " + a.getDoctorName()
                                        + " | Date: " + a.getAppointmentDate()
                                        + " | Status: " + a.getStatus() + "\n");
                    }
                }
            } catch (Exception ex) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Error retrieving appointment history: " + ex.getMessage());
            }
        });

        JPanel tab = wrapTab(formPanel, viewButton, statusLabel);
        tab.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        return tab;
    }

    // ---------- Tab 4: Update Personal Information ----------

    private JPanel buildUpdateInfoTab() {
        JTextField patientIdField = new JTextField(15);
        JTextField contactNumberField = new JTextField(15);
        JTextField icPassportField = new JTextField(15);
        JLabel statusLabel = new JLabel(" ");
        JButton updateButton = new JButton("Update Info");

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = defaultGbc();
        addFormRow(formPanel, gbc, 0, "Patient ID:", patientIdField);
        addFormRow(formPanel, gbc, 1, "New Contact Number:", contactNumberField);
        addFormRow(formPanel, gbc, 2, "New IC/Passport Number:", icPassportField);

        updateButton.addActionListener(e -> {
            try {
                int patientId = Integer.parseInt(patientIdField.getText().trim());
                String contactNumber = contactNumberField.getText().trim();
                String icPassport = icPassportField.getText().trim();

                // Only contactNumber and icPassport are used by PatientDAO.updatePatient(...)
                Patient patient = new Patient(
                        patientId, "", "", icPassport, contactNumber, "");

                Request request = new Request(Operation.UPDATE_PATIENT_INFO, patient, "patient");
                Response response = clinic.processRequest(request);
                setStatus(statusLabel, response);
            } catch (Exception ex) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Error updating patient info: " + ex.getMessage());
            }
        });

        return wrapTab(formPanel, updateButton, statusLabel);
    }

    // ---------- Tab 5: Check Doctor Availability ----------

    @SuppressWarnings("unchecked")
    private JPanel buildCheckAvailabilityTab() {
        JTextField doctorIdField = new JTextField(15);
        JLabel statusLabel = new JLabel(" ");
        JButton checkButton = new JButton("Check Availability");
        JTextArea resultArea = new JTextArea(12, 50);
        resultArea.setEditable(false);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = defaultGbc();
        addFormRow(formPanel, gbc, 0, "Doctor ID:", doctorIdField);

        checkButton.addActionListener(e -> {
            resultArea.setText("");
            try {
                int doctorId = Integer.parseInt(doctorIdField.getText().trim());

                Request request = new Request(Operation.CHECK_DOCTOR_AVAILABILITY, doctorId, "patient");
                Response response = clinic.processRequest(request);
                setStatus(statusLabel, response);

                if (response.isSuccess() && response.getData() instanceof List) {
                    List<DoctorSchedule> schedules = (List<DoctorSchedule>) response.getData();

                    if (schedules.isEmpty()) {
                        resultArea.append("No available slots found.\n");
                    }

                    for (DoctorSchedule s : schedules) {
                        resultArea.append(
                                "Schedule #" + s.getScheduleId()
                                        + " | Date: " + s.getAvailableDate()
                                        + " | Time: " + s.getAvailableTime()
                                        + " | Status: " + s.getStatus() + "\n");
                    }
                }
            } catch (Exception ex) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Error checking doctor availability: " + ex.getMessage());
            }
        });

        JPanel tab = wrapTab(formPanel, checkButton, statusLabel);
        tab.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        return tab;
    }

    // ---------- Shared helpers ----------

    private GridBagConstraints defaultGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        return gbc;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void setStatus(JLabel statusLabel, Response response) {
        if (response.isSuccess()) {
            statusLabel.setForeground(new Color(0, 128, 0));
        } else {
            statusLabel.setForeground(Color.RED);
        }
        statusLabel.setText(response.getMessage());
    }

    private JPanel wrapTab(JPanel formPanel, JButton actionButton, JLabel statusLabel) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(actionButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(statusLabel, BorderLayout.NORTH);
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        JPanel tab = new JPanel(new BorderLayout(10, 10));
        tab.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        tab.add(topPanel, BorderLayout.NORTH);
        return tab;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PatientMainFrame().setVisible(true));
    }
}
