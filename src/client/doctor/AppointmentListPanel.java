package client.doctor;

import model.Appointment;
import shared.Response;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AppointmentListPanel extends JPanel {

    private final DoctorServiceProxy proxy;

    public AppointmentListPanel(DoctorServiceProxy proxy) {

        this.proxy = proxy;
        setLayout(new BorderLayout());
        setOpaque(false);

        JTextField doctorIdField = DoctorTheme.createStyledTextField();
        JLabel statusLabel = DoctorTheme.createStatusLabel();
        JButton loadButton = DoctorTheme.createPrimaryButton("Load Appointments");

        DefaultTableModel tableModel = new DefaultTableModel(
                new Object[]{"Appointment ID", "Patient ID", "Schedule ID", "Date", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = DoctorTheme.createStyledTable(tableModel);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = DoctorTheme.defaultGbc();
        DoctorTheme.addFormRow(formPanel, gbc, 0, "Doctor ID:", doctorIdField);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 8, 5, 8);
        formPanel.add(loadButton, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(5, 8, 5, 8);
        formPanel.add(statusLabel, gbc);

        loadButton.addActionListener(e -> loadAppointments(doctorIdField, tableModel, statusLabel));

        JPanel content = new JPanel(new BorderLayout(10, 15));
        content.setOpaque(false);
        content.add(formPanel, BorderLayout.NORTH);
        content.add(new JScrollPane(table), BorderLayout.CENTER);

        add(DoctorTheme.createCardPanel("Doctor Appointment List", content, true), BorderLayout.CENTER);
    }

    private void loadAppointments(JTextField doctorIdField, DefaultTableModel tableModel, JLabel statusLabel) {

        int doctorId;

        try {
            doctorId = Integer.parseInt(doctorIdField.getText().trim());
        } catch (NumberFormatException ex) {
            DoctorTheme.setErrorStatus(statusLabel, "Please enter a valid numeric Doctor ID.");
            return;
        }

        proxy.setRetryListener((attempt, max, backoff) ->
                statusLabel.setText("Reconnecting... attempt " + attempt + "/" + max));

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
}
