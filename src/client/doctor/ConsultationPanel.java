package client.doctor;

import model.Consultation;
import shared.Response;

import javax.swing.*;
import java.awt.*;

public class ConsultationPanel extends JPanel {

    private final DoctorServiceProxy proxy;

    private final JTextField newAppointmentIdField;
    private final JTextArea newNotesArea;
    private final JLabel createStatusLabel;

    private final JTextField consultationIdField;
    private final JTextArea notesArea;
    private final JLabel versionLabel;
    private final JLabel statusLabel;

    // The last successfully loaded consultation - its version is what we
    // send back on update, so a stale save gets rejected (optimistic locking).
    private Consultation loadedConsultation;

    public ConsultationPanel(DoctorServiceProxy proxy) {

        this.proxy = proxy;
        setLayout(new BorderLayout(10, 15));
        setOpaque(false);

        // ---------- New consultation (create) section ----------
        newAppointmentIdField = DoctorTheme.createStyledTextField();
        newNotesArea = DoctorTheme.createStyledTextArea();
        newNotesArea.setRows(4);
        createStatusLabel = DoctorTheme.createStatusLabel();
        JButton createButton = DoctorTheme.createPrimaryButton("Create New Consultation");

        JPanel createForm = new JPanel(new GridBagLayout());
        createForm.setOpaque(false);
        GridBagConstraints createGbc = DoctorTheme.defaultGbc();
        DoctorTheme.addFormRow(createForm, createGbc, 0, "Appointment ID:", newAppointmentIdField);

        createGbc.gridx = 0;
        createGbc.gridy = 1;
        createGbc.gridwidth = 2;
        createForm.add(new JScrollPane(newNotesArea), createGbc);

        createGbc.gridy = 2;
        createGbc.insets = new Insets(10, 8, 5, 8);
        createForm.add(createButton, createGbc);

        createGbc.gridy = 3;
        createForm.add(createStatusLabel, createGbc);

        add(DoctorTheme.createCardPanel("New Consultation", createForm, false), BorderLayout.NORTH);

        createButton.addActionListener(e -> createConsultation());

        // ---------- Load / update existing consultation section ----------
        consultationIdField = DoctorTheme.createStyledTextField();
        JButton loadButton = DoctorTheme.createPrimaryButton("Load");
        versionLabel = new JLabel("(not loaded)");
        versionLabel.setFont(DoctorTheme.FONT_SUBHEADER);
        versionLabel.setForeground(DoctorTheme.TEXT_DARK);

        notesArea = DoctorTheme.createStyledTextArea();
        notesArea.setRows(8);

        JButton updateButton = DoctorTheme.createPrimaryButton("Update Consultation Notes");
        statusLabel = DoctorTheme.createStatusLabel();

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = DoctorTheme.defaultGbc();
        DoctorTheme.addFormRow(formPanel, gbc, 0, "Consultation ID:", consultationIdField);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 8, 5, 8);
        formPanel.add(loadButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 8, 10, 8);
        formPanel.add(versionLabel, gbc);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setOpaque(false);
        content.add(formPanel, BorderLayout.NORTH);
        content.add(new JScrollPane(notesArea), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(updateButton, BorderLayout.NORTH);
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);
        content.add(bottomPanel, BorderLayout.SOUTH);

        add(DoctorTheme.createCardPanel("Consultation Notes", content, true), BorderLayout.CENTER);

        loadButton.addActionListener(e -> loadConsultation());
        updateButton.addActionListener(e -> updateNotes());
    }

    private void createConsultation() {

        int appointmentId;

        try {
            appointmentId = Integer.parseInt(newAppointmentIdField.getText().trim());
        } catch (NumberFormatException ex) {
            DoctorTheme.setErrorStatus(createStatusLabel, "Please enter a valid numeric Appointment ID.");
            return;
        }

        String notes = newNotesArea.getText().trim();

        if (notes.isEmpty()) {
            DoctorTheme.setErrorStatus(createStatusLabel, "Please enter consultation notes.");
            return;
        }

        Consultation consultation = new Consultation(appointmentId, notes);

        proxy.setRetryListener((attempt, max, backoff)
                -> createStatusLabel.setText("Reconnecting... attempt " + attempt + "/" + max));

        Response response = proxy.addConsultation(consultation);

        DoctorTheme.setStatus(createStatusLabel, response);

        if (response.isSuccess() && response.getData() instanceof Consultation) {

            Consultation created = (Consultation) response.getData();

            // Convenience: drop the new ID straight into the Load field below
            // so the doctor can immediately load/edit what they just created.
            consultationIdField.setText(String.valueOf(created.getConsultationId()));
            newAppointmentIdField.setText("");
            newNotesArea.setText("");
        }
    }

    private void loadConsultation() {

        int consultationId;

        try {
            consultationId = Integer.parseInt(consultationIdField.getText().trim());
        } catch (NumberFormatException ex) {
            DoctorTheme.setErrorStatus(statusLabel, "Please enter a valid numeric Consultation ID.");
            return;
        }

        proxy.setRetryListener((attempt, max, backoff)
                -> statusLabel.setText("Reconnecting... attempt " + attempt + "/" + max));

        Response response = proxy.getConsultation(consultationId);

        DoctorTheme.setStatus(statusLabel, response);

        if (response.isSuccess() && response.getData() instanceof Consultation) {

            loadedConsultation = (Consultation) response.getData();
            notesArea.setText(loadedConsultation.getConsultationNotes());
            versionLabel.setText("version: " + loadedConsultation.getVersion());

        } else {

            loadedConsultation = null;
            versionLabel.setText("(not loaded)");
        }
    }

    private void updateNotes() {

        if (loadedConsultation == null) {
            DoctorTheme.setErrorStatus(statusLabel, "Load a consultation first before updating it.");
            return;
        }

        String notes = notesArea.getText().trim();

        if (notes.isEmpty()) {
            DoctorTheme.setErrorStatus(statusLabel, "Please enter consultation notes.");
            return;
        }

        Consultation consultation = new Consultation(
                loadedConsultation.getConsultationId(),
                loadedConsultation.getAppointmentId(),
                notes,
                loadedConsultation.getVersion());

        proxy.setRetryListener((attempt, max, backoff)
                -> statusLabel.setText("Reconnecting... attempt " + attempt + "/" + max));

        Response response = proxy.updateConsultationNotes(consultation);

        DoctorTheme.setStatus(statusLabel, response);

        if (response.isSuccess() && response.getData() instanceof Consultation) {

            // Success: our local copy is now the new baseline (version bumped).
            loadedConsultation = (Consultation) response.getData();
            loadedConsultation.setVersion(loadedConsultation.getVersion() + 1);
            versionLabel.setText("version: " + loadedConsultation.getVersion());

        } else if (response.getData() instanceof Consultation) {

            // Conflict: server sent back the current row - adopt it so the
            // doctor can see the real version/notes and retry cleanly.
            loadedConsultation = (Consultation) response.getData();
            notesArea.setText(loadedConsultation.getConsultationNotes());
            versionLabel.setText("version: " + loadedConsultation.getVersion() + " (reloaded after conflict)");
        }
    }
}
