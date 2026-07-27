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
    private final DefaultTableModel tableModel;
    private final JLabel statusLabel;

    public AppointmentListPanel(DoctorServiceProxy proxy) {

        this.proxy = proxy;

        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Doctor ID:"));

        doctorIdField = new JTextField(6);
        topPanel.add(doctorIdField);

        JButton loadButton = new JButton("Load Appointments");
        topPanel.add(loadButton);

        add(topPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new Object[]{"Appointment ID", "Patient ID", "Schedule ID", "Date", "Status"}, 0);

        JTable table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        statusLabel = new JLabel(" ");
        add(statusLabel, BorderLayout.SOUTH);

        loadButton.addActionListener(e -> loadAppointments());
    }

    private void loadAppointments() {

        int doctorId;

        try {
            doctorId = Integer.parseInt(doctorIdField.getText().trim());
        } catch (NumberFormatException ex) {
            statusLabel.setText("Please enter a valid numeric Doctor ID.");
            return;
        }

        proxy.setRetryListener((attempt, max, backoff) ->
                statusLabel.setText("Reconnecting... attempt " + attempt + "/" + max));

        Response response = proxy.getAppointmentList(doctorId);

        statusLabel.setText(response.getMessage());

        tableModel.setRowCount(0);

        if (response.isSuccess() && response.getData() instanceof List) {

            @SuppressWarnings("unchecked")
            List<Appointment> appointments = (List<Appointment>) response.getData();

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
    }
}
