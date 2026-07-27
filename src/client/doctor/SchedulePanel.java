package client.doctor;

import model.DoctorSchedule;
import shared.Response;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SchedulePanel extends JPanel {

    private final DoctorServiceProxy proxy;

    private final JTextField doctorIdField;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JComboBox<String> statusCombo;
    private final JLabel statusLabel;

    private List<DoctorSchedule> currentSchedules = new ArrayList<>();

    public SchedulePanel(DoctorServiceProxy proxy) {

        this.proxy = proxy;

        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Doctor ID:"));

        doctorIdField = new JTextField(6);
        topPanel.add(doctorIdField);

        JButton loadButton = new JButton("Load Schedule");
        topPanel.add(loadButton);

        add(topPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new Object[]{"Schedule ID", "Doctor ID", "Date", "Time", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.add(new JLabel("New status for selected row:"));

        statusCombo = new JComboBox<>(new String[]{"AVAILABLE", "BOOKED"});
        bottomPanel.add(statusCombo);

        JButton updateButton = new JButton("Update Status");
        bottomPanel.add(updateButton);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(bottomPanel, BorderLayout.NORTH);

        statusLabel = new JLabel(" ");
        southPanel.add(statusLabel, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);

        loadButton.addActionListener(e -> loadSchedule());
        updateButton.addActionListener(e -> updateSelectedStatus());
    }

    private void loadSchedule() {

        int doctorId;

        try {
            doctorId = Integer.parseInt(doctorIdField.getText().trim());
        } catch (NumberFormatException ex) {
            statusLabel.setText("Please enter a valid numeric Doctor ID.");
            return;
        }

        proxy.setRetryListener((attempt, max, backoff) ->
                statusLabel.setText("Reconnecting... attempt " + attempt + "/" + max));

        Response response = proxy.getSchedule(doctorId);

        statusLabel.setText(response.getMessage());

        tableModel.setRowCount(0);
        currentSchedules.clear();

        if (response.isSuccess() && response.getData() instanceof List) {

            @SuppressWarnings("unchecked")
            List<DoctorSchedule> schedules = (List<DoctorSchedule>) response.getData();

            currentSchedules = schedules;

            for (DoctorSchedule schedule : schedules) {
                tableModel.addRow(new Object[]{
                        schedule.getScheduleId(),
                        schedule.getDoctorId(),
                        schedule.getAvailableDate(),
                        schedule.getAvailableTime(),
                        schedule.getStatus()
                });
            }
        }
    }

    private void updateSelectedStatus() {

        int selectedRow = table.getSelectedRow();

        if (selectedRow < 0 || selectedRow >= currentSchedules.size()) {
            statusLabel.setText("Please select a schedule row first.");
            return;
        }

        DoctorSchedule selected = currentSchedules.get(selectedRow);
        String newStatus = (String) statusCombo.getSelectedItem();

        DoctorSchedule updated = new DoctorSchedule(
                selected.getScheduleId(),
                selected.getDoctorId(),
                selected.getAvailableDate(),
                selected.getAvailableTime(),
                newStatus
        );

        proxy.setRetryListener((attempt, max, backoff) ->
                statusLabel.setText("Reconnecting... attempt " + attempt + "/" + max));

        Response response = proxy.updateSchedule(updated);

        statusLabel.setText(response.getMessage());

        if (response.isSuccess()) {
            loadSchedule();
        }
    }
}
