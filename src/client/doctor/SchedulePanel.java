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

    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JComboBox<String> statusCombo;
    private final JLabel statusLabel;

    private List<DoctorSchedule> currentSchedules = new ArrayList<>();

    public SchedulePanel(DoctorServiceProxy proxy) {

        this.proxy = proxy;
        setLayout(new BorderLayout());
        setOpaque(false);

        JTextField doctorIdField = DoctorTheme.createStyledTextField();
        statusLabel = DoctorTheme.createStatusLabel();
        JButton loadButton = DoctorTheme.createPrimaryButton("Load Schedule");

        tableModel = new DefaultTableModel(
                new Object[]{"Schedule ID", "Doctor ID", "Date", "Time", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = DoctorTheme.createStyledTable(tableModel);

        statusCombo = new JComboBox<>(new String[]{"AVAILABLE", "BOOKED"});
        statusCombo.setFont(DoctorTheme.FONT_INPUT);
        JButton updateButton = DoctorTheme.createPrimaryButton("Update Status");

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = DoctorTheme.defaultGbc();
        DoctorTheme.addFormRow(formPanel, gbc, 0, "Doctor ID:", doctorIdField);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 8, 5, 8);
        formPanel.add(loadButton, gbc);

        JPanel updateRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        updateRow.setOpaque(false);
        updateRow.add(new JLabel("New status for selected row:") {
            {
                setFont(DoctorTheme.FONT_LABEL);
            }
        });
        updateRow.add(statusCombo);
        updateRow.add(updateButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 8, 5, 8);
        formPanel.add(updateRow, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(5, 8, 5, 8);
        formPanel.add(statusLabel, gbc);

        loadButton.addActionListener(e -> loadSchedule(doctorIdField));
        updateButton.addActionListener(e -> updateSelectedStatus());

        JPanel content = new JPanel(new BorderLayout(10, 15));
        content.setOpaque(false);
        content.add(formPanel, BorderLayout.NORTH);
        content.add(new JScrollPane(table), BorderLayout.CENTER);

        add(DoctorTheme.createCardPanel("Doctor Schedule Management", content, true), BorderLayout.CENTER);
    }

    private void loadSchedule(JTextField doctorIdField) {

        int doctorId;

        try {
            doctorId = Integer.parseInt(doctorIdField.getText().trim());
        } catch (NumberFormatException ex) {
            DoctorTheme.setErrorStatus(statusLabel, "Please enter a valid numeric Doctor ID.");
            return;
        }

        proxy.setRetryListener((attempt, max, backoff)
                -> statusLabel.setText("Reconnecting... attempt " + attempt + "/" + max));

        Response response = proxy.getSchedule(doctorId);

        tableModel.setRowCount(0);
        currentSchedules.clear();

        if (response.isSuccess() && response.getData() instanceof List) {

            @SuppressWarnings("unchecked")
            List<DoctorSchedule> schedules = (List<DoctorSchedule>) response.getData();

            currentSchedules = schedules;

            if (schedules.isEmpty()) {
                DoctorTheme.setErrorStatus(statusLabel, "No schedule found for Doctor ID: " + doctorId);
            } else {
                DoctorTheme.setStatus(statusLabel, response);
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
        } else {
            DoctorTheme.setStatus(statusLabel, response);
        }
    }

    private void updateSelectedStatus() {

        int selectedRow = table.getSelectedRow();

        if (selectedRow < 0 || selectedRow >= currentSchedules.size()) {
            DoctorTheme.setErrorStatus(statusLabel, "Please select a schedule row first.");
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

        proxy.setRetryListener((attempt, max, backoff)
                -> statusLabel.setText("Reconnecting... attempt " + attempt + "/" + max));

        Response response = proxy.updateSchedule(updated);

        DoctorTheme.setStatus(statusLabel, response);

        if (response.isSuccess()) {

            int doctorId = selected.getDoctorId();
            Response reload = proxy.getSchedule(doctorId);

            tableModel.setRowCount(0);

            if (reload.isSuccess() && reload.getData() instanceof List) {
                @SuppressWarnings("unchecked")
                List<DoctorSchedule> schedules = (List<DoctorSchedule>) reload.getData();
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
    }
}
