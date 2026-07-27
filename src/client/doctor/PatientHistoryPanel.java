package client.doctor;

import model.MedicalRecord;
import shared.Response;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PatientHistoryPanel extends JPanel {

    private final DoctorServiceProxy proxy;

    private final JTextField patientIdField;
    private final DefaultTableModel tableModel;
    private final JLabel statusLabel;

    public PatientHistoryPanel(DoctorServiceProxy proxy) {

        this.proxy = proxy;

        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Patient ID:"));

        patientIdField = new JTextField(6);
        topPanel.add(patientIdField);

        JButton loadButton = new JButton("Load Patient History");
        topPanel.add(loadButton);

        add(topPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new Object[]{"Record ID", "Patient ID", "Diagnosis"}, 0);

        JTable table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        statusLabel = new JLabel(" ");
        add(statusLabel, BorderLayout.SOUTH);

        loadButton.addActionListener(e -> loadHistory());
    }

    private void loadHistory() {

        int patientId;

        try {
            patientId = Integer.parseInt(patientIdField.getText().trim());
        } catch (NumberFormatException ex) {
            statusLabel.setText("Please enter a valid numeric Patient ID.");
            return;
        }

        proxy.setRetryListener((attempt, max, backoff) ->
                statusLabel.setText("Reconnecting... attempt " + attempt + "/" + max));

        Response response = proxy.getPatientHistory(patientId);

        statusLabel.setText(response.getMessage());

        tableModel.setRowCount(0);

        if (response.isSuccess() && response.getData() instanceof List) {

            @SuppressWarnings("unchecked")
            List<MedicalRecord> records = (List<MedicalRecord>) response.getData();

            for (MedicalRecord record : records) {
                tableModel.addRow(new Object[]{
                        record.getRecordId(),
                        record.getPatientId(),
                        record.getDiagnosis()
                });
            }
        }
    }
}
