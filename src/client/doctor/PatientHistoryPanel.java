package client.doctor;

import model.MedicalRecord;
import shared.Response;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PatientHistoryPanel extends JPanel {

    private final DoctorServiceProxy proxy;

    public PatientHistoryPanel(DoctorServiceProxy proxy) {

        this.proxy = proxy;
        setLayout(new BorderLayout());
        setOpaque(false);

        JTextField patientIdField = DoctorTheme.createStyledTextField();
        JLabel statusLabel = DoctorTheme.createStatusLabel();
        JButton loadButton = DoctorTheme.createPrimaryButton("Load Patient History");

        DefaultTableModel tableModel = new DefaultTableModel(
                new Object[]{"Record ID", "Patient ID", "Diagnosis"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = DoctorTheme.createStyledTable(tableModel);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = DoctorTheme.defaultGbc();
        DoctorTheme.addFormRow(formPanel, gbc, 0, "Patient ID:", patientIdField);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 8, 5, 8);
        formPanel.add(loadButton, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(5, 8, 5, 8);
        formPanel.add(statusLabel, gbc);

        loadButton.addActionListener(e -> loadHistory(patientIdField, tableModel, statusLabel));

        JPanel content = new JPanel(new BorderLayout(10, 15));
        content.setOpaque(false);
        content.add(formPanel, BorderLayout.NORTH);
        content.add(new JScrollPane(table), BorderLayout.CENTER);

        add(DoctorTheme.createCardPanel("Patient Medical History", content, true), BorderLayout.CENTER);
    }

    private void loadHistory(JTextField patientIdField, DefaultTableModel tableModel, JLabel statusLabel) {

        int patientId;

        try {
            patientId = Integer.parseInt(patientIdField.getText().trim());
        } catch (NumberFormatException ex) {
            DoctorTheme.setErrorStatus(statusLabel, "Please enter a valid numeric Patient ID.");
            return;
        }

        proxy.setRetryListener((attempt, max, backoff) ->
                statusLabel.setText("Reconnecting... attempt " + attempt + "/" + max));

        Response response = proxy.getPatientHistory(patientId);

        tableModel.setRowCount(0);

        if (response.isSuccess() && response.getData() instanceof List) {

            @SuppressWarnings("unchecked")
            List<MedicalRecord> records = (List<MedicalRecord>) response.getData();

            if (records.isEmpty()) {
                DoctorTheme.setErrorStatus(statusLabel, "No medical records found for Patient ID: " + patientId);
            } else {
                DoctorTheme.setStatus(statusLabel, response);
                for (MedicalRecord record : records) {
                    tableModel.addRow(new Object[]{
                            record.getRecordId(),
                            record.getPatientId(),
                            record.getDiagnosis()
                    });
                }
            }
        } else {
            DoctorTheme.setStatus(statusLabel, response);
        }
    }
}
