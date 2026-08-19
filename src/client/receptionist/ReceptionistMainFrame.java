package client.receptionist;

import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import security.SslNoHostnameCheckSocketFactory;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import model.Patient;
import rmi.ClinicRemote;
import security.SslConfig;
import shared.Operation;
import shared.Request;
import shared.Response;

/**
 * GUI entry point for the Receptionist role (single-file version). Redesigned
 * with modern medical center theme matching PatientMainFrame.
 */
public class ReceptionistMainFrame extends JFrame {

    private ClinicRemote clinic;

    // Form Text Fields
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField icPassportField;
    private JTextField contactNumberField;
    private JTextField medicalRecordIdField;

    // Form Buttons & Status
    private JButton registerButton;
    private JButton clearButton;
    private JLabel statusLabel;
    private JLabel serverStatusLabel;

    // Theme Colors (Matching Patient Portal)
    private static final Color PRIMARY_COLOR = new Color(15, 118, 110);    // Medical Teal
    private static final Color BG_COLOR = new Color(241, 245, 249);         // Slate Gray
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_DARK = new Color(30, 41, 59);
    private static final Color SUCCESS_COLOR = new Color(22, 101, 52);
    private static final Color ERROR_COLOR = new Color(153, 27, 27);

    // Fonts
    private static final Font FONT_HEADER = new Font("SansSerif", Font.BOLD, 18);
    private static final Font FONT_SUBHEADER = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font FONT_LABEL = new Font("SansSerif", Font.BOLD, 13);
    private static final Font FONT_INPUT = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font FONT_BTN = new Font("SansSerif", Font.BOLD, 13);

    public ReceptionistMainFrame() {
        super("BrightCare Medical Center - Receptionist Desk Portal");

        setSystemLookAndFeel();
        connectToServer();
        initComponents();
        buildUI();

        setSize(750, 530);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fallback to default Swing look
        }
    }

    private void connectToServer() {
        try {
            Registry registry = LocateRegistry.getRegistry(
                    "localhost", 1099, new SslNoHostnameCheckSocketFactory());
            clinic = (ClinicRemote) registry.lookup("ClinicService");
        } catch (Exception e) {
            clinic = null;
        }
    }

    private void initComponents() {
        firstNameField = createStyledTextField();
        lastNameField = createStyledTextField();
        icPassportField = createStyledTextField();
        contactNumberField = createStyledTextField();
        medicalRecordIdField = createStyledTextField();

        registerButton = createPrimaryButton("Register Patient");
        clearButton = createSecondaryButton("Clear Form");
        statusLabel = createStatusLabel();

        registerButton.addActionListener(e -> onRegister());
        clearButton.addActionListener(e -> clearFields());
    }

    private void buildUI() {
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(BG_COLOR);

        // Header Banner
        mainContainer.add(createHeaderPanel(), BorderLayout.NORTH);

        // Form Layout
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = defaultGbc();

        addFormRow(formPanel, gbc, 0, "First Name:", firstNameField);
        addFormRow(formPanel, gbc, 1, "Last Name:", lastNameField);
        addFormRow(formPanel, gbc, 2, "IC / Passport Number:", icPassportField);
        addFormRow(formPanel, gbc, 3, "Contact Number:", contactNumberField);
        addFormRow(formPanel, gbc, 4, "Medical Record ID:", medicalRecordIdField);

        // Button Layout
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(registerButton);
        buttonPanel.add(clearButton);

        // Card Container
        JPanel card = createCardPanel("New Patient Intake Form");
        JPanel cardContent = new JPanel(new BorderLayout(10, 10));
        cardContent.setOpaque(false);
        cardContent.add(statusLabel, BorderLayout.NORTH);
        cardContent.add(formPanel, BorderLayout.CENTER);
        cardContent.add(buttonPanel, BorderLayout.SOUTH);

        card.add(cardContent, BorderLayout.NORTH);

        JPanel outerCardWrapper = new JPanel(new BorderLayout());
        outerCardWrapper.setBackground(BG_COLOR);
        outerCardWrapper.setBorder(new EmptyBorder(10, 10, 10, 10));
        outerCardWrapper.add(card, BorderLayout.CENTER);

        // Tabbed Pane Wrapper
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(FONT_LABEL);
        tabbedPane.addTab("  Register Patient  ", outerCardWrapper);

        JPanel tabWrapper = new JPanel(new BorderLayout());
        tabWrapper.setBackground(BG_COLOR);
        tabWrapper.setBorder(new EmptyBorder(15, 15, 15, 15));
        tabWrapper.add(tabbedPane, BorderLayout.CENTER);

        mainContainer.add(tabWrapper, BorderLayout.CENTER);
        setContentPane(mainContainer);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_COLOR);
        header.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("BrightCare Medical Center");
        title.setFont(FONT_HEADER);
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Receptionist Desk Portal");
        subtitle.setFont(FONT_SUBHEADER);
        subtitle.setForeground(new Color(204, 251, 241));

        JPanel titles = new JPanel(new GridLayout(2, 1, 0, 2));
        titles.setOpaque(false);
        titles.add(title);
        titles.add(subtitle);

        serverStatusLabel = new JLabel();
        serverStatusLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        updateServerStatus(clinic != null);

        header.add(titles, BorderLayout.WEST);
        header.add(serverStatusLabel, BorderLayout.EAST);
        return header;
    }

    private void updateServerStatus(boolean connected) {
        if (connected) {
            serverStatusLabel.setText("● Server Connected");
            serverStatusLabel.setForeground(new Color(187, 247, 208));
        } else {
            serverStatusLabel.setText("● Offline");
            serverStatusLabel.setForeground(new Color(254, 202, 202));
        }
    }

    // ---------- UI Helper Factory Methods ----------
    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setFont(FONT_INPUT);
        field.setBorder(new CompoundBorder(
                new LineBorder(new Color(203, 213, 225), 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
        return field;
    }

    private JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BTN);
        btn.setBackground(PRIMARY_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        return btn;
    }

    private JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BTN);
        btn.setBackground(new Color(226, 232, 240));
        btn.setForeground(TEXT_DARK);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        return btn;
    }

    private JLabel createStatusLabel() {
        JLabel label = new JLabel(" ", SwingConstants.LEFT);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        label.setBorder(new EmptyBorder(5, 5, 5, 5));
        return label;
    }

    private JPanel createCardPanel(String titleText) {
        JPanel card = new JPanel(new BorderLayout(15, 15));
        card.setBackground(CARD_BG);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(15, 20, 15, 20)
        ));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("SansSerif", Font.BOLD, 15));
        title.setForeground(PRIMARY_COLOR);
        title.setBorder(new EmptyBorder(0, 0, 10, 0));

        card.add(title, BorderLayout.NORTH);
        return card;
    }

    private GridBagConstraints defaultGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        return gbc;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel label = new JLabel(labelText);
        label.setFont(FONT_LABEL);
        label.setForeground(TEXT_DARK);
        panel.add(label, gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    // ---------- Event Handlers ----------
    private void onRegister() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String icPassport = icPassportField.getText().trim();
        String contactNumber = contactNumberField.getText().trim();
        String medicalRecordId = medicalRecordIdField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || icPassport.isEmpty()
                || contactNumber.isEmpty() || medicalRecordId.isEmpty()) {
            statusLabel.setForeground(ERROR_COLOR);
            statusLabel.setText("✖ Please fill in all fields before submitting.");
            return;
        }

        Patient patient = new Patient(firstName, lastName, icPassport, contactNumber, medicalRecordId);

        try {
            Request request = new Request(Operation.REGISTER_PATIENT, patient, "receptionist");
            Response response = clinic.processRequest(request);
            if (response.isSuccess()) {
                statusLabel.setForeground(SUCCESS_COLOR);
                statusLabel.setText("✔ Success: " + response.getMessage());
                clearFields();
            } else {
                statusLabel.setForeground(ERROR_COLOR);
                statusLabel.setText("✖ Failed: " + response.getMessage());
            }
        } catch (Exception ex) {
            statusLabel.setForeground(ERROR_COLOR);
            statusLabel.setText("✖ Error registering patient: " + ex.getMessage());
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
        SslConfig.configureClient();
        SwingUtilities.invokeLater(() -> new ReceptionistMainFrame().setVisible(true));
    }
}
