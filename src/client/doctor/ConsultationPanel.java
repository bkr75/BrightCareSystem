package client.doctor;

import model.Consultation;
import shared.Response;

import javax.swing.*;
import java.awt.*;

public class ConsultationPanel extends JPanel {

    private final DoctorServiceProxy proxy;

    private final JTextField consultationIdField;
    private final JTextArea notesArea;
    private final JLabel versionLabel;
    private final JLabel statusLabel;

    // The last successfully loaded consultation - its version is what we
    // send back on update, so a stale save gets rejected (optimistic locking).
    private Consultation loadedConsultation;

    public ConsultationPanel(DoctorServiceProxy proxy) {

        this.proxy = proxy;

        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Consultation ID:"));

        consultationIdField = new JTextField(6);
        topPanel.add(consultationIdField);

        JButton loadButton = new JButton("Load");
        topPanel.add(loadButton);

        versionLabel = new JLabel("(not loaded)");
        topPanel.add(versionLabel);

        add(topPanel, BorderLayout.NORTH);

        notesArea = new JTextArea();
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        add(new JScrollPane(notesArea), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());

        JButton updateButton = new JButton("Update Consultation Notes");
        bottomPanel.add(updateButton, BorderLayout.NORTH);

        statusLabel = new JLabel(" ");
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        loadButton.addActionListener(e -> loadConsultation());
        updateButton.addActionListener(e -> updateNotes());
    }

    private void loadConsultation() {

        int consultationId;

        try {
            consultationId = Integer.parseInt(consultationIdField.getText().trim());
        } catch (NumberFormatException ex) {
            statusLabel.setText("Please enter a valid numeric Consultation ID.");
            return;
        }

        proxy.setRetryListener((attempt, max, backoff) ->
                statusLabel.setText("Reconnecting... attempt " + attempt + "/" + max));

        Response response = proxy.getConsultation(consultationId);

        statusLabel.setText(response.getMessage());

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
            statusLabel.setText("Load a consultation first before updating it.");
            return;
        }

        String notes = notesArea.getText().trim();

        if (notes.isEmpty()) {
            statusLabel.setText("Please enter consultation notes.");
            return;
        }

        Consultation consultation = new Consultation(
                loadedConsultation.getConsultationId(),
                loadedConsultation.getAppointmentId(),
                notes,
                loadedConsultation.getVersion());

        proxy.setRetryListener((attempt, max, backoff) ->
                statusLabel.setText("Reconnecting... attempt " + attempt + "/" + max));

        Response response = proxy.updateConsultationNotes(consultation);

        statusLabel.setText(response.getMessage());

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
