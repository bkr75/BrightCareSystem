package client.receptionist;

import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.swing.*;

import model.Patient;
import rmi.ClinicRemote;
import shared.Operation;
import shared.Request;
import shared.Response;

/**
 * GUI entry point for the Receptionist role (single-file version,
 * same idea as client.doctor.DoctorMainFrame but everything kept
 * in one class since there's only one feature: Patient Registration).
 *
 * The original CLI class, ReceptionistClient, is left untouched —
 * this is an additional GUI entry point, not a replacement.
 */
public class ReceptionistMainFrame extends JFrame {

    private ClinicRemote clinic;

    private final JTextField firstNameField = new JTextField(20);
    private final JTextField lastNameField = new JTextField(20);
    private final JTextField icPassportField = new JTextField(20);
    private final JTextField contactNumberField = new JTextField(20);
    private final JTextField medicalRecordIdField = new JTextField(20);

    private final JButton registerButton = new JButton("Register Patient");
    private final JButton clearButton = new JButton("Clear");
    private final JLabel statusLabel = new JLabel(" ");

    public ReceptionistMainFrame() {
        super("BrightCare - Receptionist Module");
        connectToServer();
        buildUI();
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

    private void buildUI() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        addFormRow(formPanel, gbc, 0, "First Name:", firstNameField);
        addFormRow(formPanel, gbc, 1, "Last Name:", lastNameField);
        addFormRow(formPanel, gbc, 2, "IC/Passport Number:", icPassportField);
        addFormRow(formPanel, gbc, 3, "Contact Number:", contactNumberField);
        addFormRow(formPanel, gbc, 4, "Medical Record ID:", medicalRecordIdField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(registerButton);
        buttonPanel.add(clearButton);

        JPanel registerTab = new JPanel(new BorderLayout(10, 10));
        registerTab.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        registerTab.add(statusLabel, BorderLayout.NORTH);
        registerTab.add(formPanel, BorderLayout.CENTER);
        registerTab.add(buttonPanel, BorderLayout.SOUTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Register Patient", registerTab);

        setContentPane(tabbedPane);
        setSize(700, 450);
        setLocationRelativeTo(null);

        registerButton.addActionListener(e -> onRegister());
        clearButton.addActionListener(e -> clearFields());
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void onRegister() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String icPassport = icPassportField.getText().trim();
        String contactNumber = contactNumberField.getText().trim();
        String medicalRecordId = medicalRecordIdField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || icPassport.isEmpty()
                || contactNumber.isEmpty() || medicalRecordId.isEmpty()) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Please fill in all fields.");
            return;
        }

        Patient patient = new Patient(firstName, lastName, icPassport, contactNumber, medicalRecordId);

        try {
            Request request = new Request(Operation.REGISTER_PATIENT, patient, "receptionist");
            Response response = clinic.processRequest(request);
            if (response.isSuccess()) {
                statusLabel.setForeground(new Color(0, 128, 0));
                statusLabel.setText("Success: " + response.getMessage());
                clearFields();
            } else {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Failed: " + response.getMessage());
            }
        } catch (Exception ex) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Error registering patient: " + ex.getMessage());
        }
    }

    private void clearFields() {
        firstNameField.setText("");
        lastNameField.setText("");
        icPassportField.setText("");
        contactNumberField.setText("");
        medicalRecordIdField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ReceptionistMainFrame().setVisible(true));
    }
}
