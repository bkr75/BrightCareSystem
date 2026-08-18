package client.doctor;

import model.Appointment;
import shared.Response;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AppointmentListPanel extends JPanel {

    private final DoctorServiceProxy proxy;

    private final JTextField doctorIdField;
    private final JLabel statusLabel;
    private final DefaultTableModel tableModel;
    private final JTable table;

    // Remembers the last doctor ID a search was run for, so we can refresh
    // the table after completing an appointment without asking again.
    private int lastLoadedDoctorId = -1;

    public AppointmentListPanel(DoctorServiceProxy proxy) {

        this.proxy = proxy;
        setLayout(new BorderLayout());
        setOpaque(false);

        doctorIdField = DoctorTheme.createStyledTextField();
        statusLabel = DoctorTheme.createStatusLabel();
        JButton loadButton = DoctorTheme.createPrimaryButton("Load Appointments");
        JButton completeButton = DoctorTheme.createSecondaryButton("Mark Selected as Completed");

        tableModel = new DefaultTableModel(
                new Object[]{"Appointment ID", "Patient ID", "Schedule ID", "Date", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = DoctorTheme.createStyledTable(tableModel);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = DoctorTheme.defaultGbc();
        DoctorTheme.addFormRow(formPanel, gbc, 0, "Doctor ID:", doctorIdField);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 8, 5, 8);
        formPanel.add(loadButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 8, 5, 8);
        formPanel.add(completeButton, gbc);

        gbc.gridy = 3;
        formPanel.add(statusLabel, gbc);

        loadButton.addActionListener(e -> loadAppointments());
        completeButton.addActionListener(e -> completeSelected());

        JPanel content = new JPanel(new BorderLayout(10, 15));
        content.setOpaque(false);
        content.add(formPanel, BorderLayout.NORTH);
        content.add(new JScrollPane(table), BorderLayout.CENTER);

        add(DoctorTheme.createCardPanel("Doctor Appointment List", content, true), BorderLayout.CENTER);
    }

    private void loadAppointments() {

        int doctorId;

        try {
            doctorId = Integer.parseInt(doctorIdField.getText().trim());
        } catch (NumberFormatException ex) {
            DoctorTheme.setErrorStatus(statusLabel, "Please enter a valid numeric Doctor ID.");
            return;
        }

        lastLoadedDoctorId = doctorId;

        proxy.setRetryListener((attempt, max, backoff)
                -> statusLabel.setText("Reconnecting... attempt " + attempt + "/" + max));

        Response response = proxy.getAppointmentList(doctorId);

        tableModel.setRowCount(0);

        if (response.isSuccess() && response.getData() instanceof List) {

            @SuppressWarnings("unchecked")
            List<Appointment> appointments = (List<Appointment>) response.getData();

            if (appointments.isEmpty()) {
                DoctorTheme.setErrorStatus(statusLabel, "No appointments found for Doctor ID: " + doctorId);
            } else {
                DoctorTheme.setStatus(statusLabel, response);
                for (Appointment appointment : appointments) {
                    tableModel.addRow(new Object[]{
                        appointment.getAppointmentId(),
                        appointment.getPatientId(),
                        appointment.getScheduleId(),
                        appointment.getAppointmentDate(),
                        appointment.getStatus()
                    });
                }
            }
        } else {
            DoctorTheme.setStatus(statusLabel, response);
        }
    }

    private void completeSelected() {

        int selectedRow = table.getSelectedRow();

        if (selectedRow < 0) {
            DoctorTheme.setErrorStatus(statusLabel, "Please select an appointment row first.");
            return;
        }

        int appointmentId = (Integer) tableModel.getValueAt(selectedRow, 0);

        proxy.setRetryListener((attempt, max, backoff)
                -> statusLabel.setText("Reconnecting... attempt " + attempt + "/" + max));

        Response response = proxy.completeAppointment(appointmentId);

        DoctorTheme.setStatus(statusLabel, response);

        if (response.isSuccess() && lastLoadedDoctorId > 0) {
            // Refresh the table so the updated status is visible immediately.
            doctorIdField.setText(String.valueOf(lastLoadedDoctorId));
            loadAppointments();
        }
    }
}
