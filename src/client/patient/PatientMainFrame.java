package client.patient;

import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Date;
import java.util.List;
import security.SslNoHostnameCheckSocketFactory;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import model.Appointment;
import model.DoctorSchedule;
import model.LoginData;
import model.Patient;
import rmi.ClinicRemote;
import security.SslConfig;
import shared.Operation;
import shared.Request;
import shared.Response;

public class PatientMainFrame extends JFrame {

    private ClinicRemote clinic;
    private JLabel serverStatusLabel;
    private String username;

    // Theme Colors
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

    public PatientMainFrame() {
        super("BrightCare Medical Center - Patient Self-Service Portal");

        setSystemLookAndFeel();
        connectToServer();

        setSize(850, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        showLoginScreen();
    }

    // ---------- Login screen (shown first) ----------
    private void showLoginScreen() {

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(BG_COLOR);

        JTextField usernameField = createStyledTextField();
        JPasswordField passwordField = new JPasswordField(18);
        passwordField.setFont(FONT_INPUT);
        passwordField.setBorder(usernameField.getBorder());

        JLabel statusLabel = createStatusLabel();
        JButton loginButton = createPrimaryButton("Log In");

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = defaultGbc();
        addFormRow(form, gbc, 0, "Username:", usernameField);
        addFormRow(form, gbc, 1, "Password:", passwordField);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.insets = new Insets(15, 8, 5, 8);
        form.add(loginButton, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(5, 8, 5, 8);
        form.add(statusLabel, gbc);

        wrapper.add(createCardPanel("Patient Login", form, false));

        Runnable attemptLogin = () -> {

            String enteredUsername = usernameField.getText().trim();
            String enteredPassword = new String(passwordField.getPassword()).trim();

            if (enteredUsername.isEmpty() || enteredPassword.isEmpty()) {
                setErrorStatus(statusLabel, "Please enter both username and password.");
                return;
            }

            if (clinic == null) {
                setErrorStatus(statusLabel, "Not connected to the server.");
                return;
            }

            try {
                Request request = new Request(Operation.LOGIN,
                        new LoginData(enteredUsername, enteredPassword), enteredUsername);
                Response response = clinic.processRequest(request);

                if (response.isSuccess()) {
                    username = enteredUsername;
                    showPortal();
                } else {
                    setErrorStatus(statusLabel, response.getMessage());
                }
            } catch (Exception ex) {
                setErrorStatus(statusLabel, "Login error: " + ex.getMessage());
            }
        };

        loginButton.addActionListener(e -> attemptLogin.run());
        passwordField.addActionListener(e -> attemptLogin.run());

        setContentPane(wrapper);
        revalidate();
        repaint();
    }

    // ---------- Main portal (shown after a successful login) ----------
    private void showPortal() {

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(BG_COLOR);

        mainContainer.add(createHeaderPanel(), BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(FONT_LABEL);
        tabbedPane.addTab("  Book Appointment  ", buildBookAppointmentTab());
        tabbedPane.addTab("  Cancel Appointment  ", buildCancelAppointmentTab());
        tabbedPane.addTab("  Appointment History  ", buildViewHistoryTab());
        tabbedPane.addTab("  Update Info  ", buildUpdateInfoTab());
        tabbedPane.addTab("  Doctor Availability  ", buildCheckAvailabilityTab());

        JPanel tabWrapper = new JPanel(new BorderLayout());
        tabWrapper.setBackground(BG_COLOR);
        tabWrapper.setBorder(new EmptyBorder(15, 15, 15, 15));
        tabWrapper.add(tabbedPane, BorderLayout.CENTER);

        mainContainer.add(tabWrapper, BorderLayout.CENTER);

        setContentPane(mainContainer);
        revalidate();
        repaint();
    }

    private void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_COLOR);
        header.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("BrightCare Medical Center");
        title.setFont(FONT_HEADER);
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Patient Self-Service Portal");
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

    private void connectToServer() {
        try {
            Registry registry = LocateRegistry.getRegistry(
                    "localhost", 1099, new SslNoHostnameCheckSocketFactory());
            clinic = (ClinicRemote) registry.lookup("ClinicService");
        } catch (Exception e) {
            clinic = null;
        }
    }

    // ---------- Tab 1: Book Appointment ----------
    private JPanel buildBookAppointmentTab() {
        JTextField patientIdField = createStyledTextField();
        JTextField doctorIdField = createStyledTextField();
        JTextField scheduleIdField = createStyledTextField();
        JTextField dateField = createStyledTextField();
        JLabel statusLabel = createStatusLabel();
        JButton bookButton = createPrimaryButton("Book Appointment");

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = defaultGbc();

        addFormRow(formPanel, gbc, 0, "Patient ID:", patientIdField);
        addFormRow(formPanel, gbc, 1, "Doctor ID:", doctorIdField);
        addFormRow(formPanel, gbc, 2, "Schedule ID:", scheduleIdField);
        addFormRow(formPanel, gbc, 3, "Appointment Date (YYYY-MM-DD):", dateField);

        // Add Button directly to Grid Row 4
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.insets = new Insets(15, 8, 5, 8);
        formPanel.add(bookButton, gbc);

        // Add Status Label to Grid Row 5
        gbc.gridy = 5;
        gbc.insets = new Insets(5, 8, 5, 8);
        formPanel.add(statusLabel, gbc);

        bookButton.addActionListener(e -> {
            try {
                int patientId = Integer.parseInt(patientIdField.getText().trim());
                int doctorId = Integer.parseInt(doctorIdField.getText().trim());
                int scheduleId = Integer.parseInt(scheduleIdField.getText().trim());
                Date appointmentDate = Date.valueOf(dateField.getText().trim());

                Appointment appointment = new Appointment(
                        patientId, doctorId, scheduleId, appointmentDate, "BOOKED");

                Request request = new Request(Operation.BOOK_APPOINTMENT, appointment, username);
                Response response = clinic.processRequest(request);
                setStatus(statusLabel, response);
            } catch (Exception ex) {
                setErrorStatus(statusLabel, "Error booking appointment: " + ex.getMessage());
            }
        });

        return createCardPanel("Schedule a New Appointment", formPanel, false);
    }

    // ---------- Tab 2: Cancel Appointment ----------
    private JPanel buildCancelAppointmentTab() {
        JTextField appointmentIdField = createStyledTextField();
        JLabel statusLabel = createStatusLabel();
        JButton cancelButton = createDangerButton("Cancel Appointment");

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = defaultGbc();
        addFormRow(formPanel, gbc, 0, "Appointment ID:", appointmentIdField);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.insets = new Insets(15, 8, 5, 8);
        formPanel.add(cancelButton, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(5, 8, 5, 8);
        formPanel.add(statusLabel, gbc);

        cancelButton.addActionListener(e -> {
            try {
                int appointmentId = Integer.parseInt(appointmentIdField.getText().trim());

                Request request = new Request(Operation.CANCEL_APPOINTMENT, appointmentId, username);
                Response response = clinic.processRequest(request);
                setStatus(statusLabel, response);
            } catch (Exception ex) {
                setErrorStatus(statusLabel, "Error cancelling appointment: " + ex.getMessage());
            }
        });

        return createCardPanel("Cancel Existing Appointment", formPanel, false);
    }

    // ---------- Tab 3: View Appointment History ----------
    @SuppressWarnings("unchecked")
    private JPanel buildViewHistoryTab() {
        JTextField patientIdField = createStyledTextField();
        JLabel statusLabel = createStatusLabel();
        JButton viewButton = createPrimaryButton("View History");

        String[] columns = {"Appt ID", "Doctor Name", "Appointment Date", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable historyTable = createStyledTable(tableModel);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = defaultGbc();
        addFormRow(formPanel, gbc, 0, "Patient ID:", patientIdField);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 8, 5, 8);
        formPanel.add(viewButton, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(5, 8, 5, 8);
        formPanel.add(statusLabel, gbc);

        viewButton.addActionListener(e -> {
            tableModel.setRowCount(0);
            try {
                int patientId = Integer.parseInt(patientIdField.getText().trim());

                Request request = new Request(Operation.VIEW_APPOINTMENT_HISTORY, patientId, username);
                Response response = clinic.processRequest(request);

                if (response.isSuccess() && response.getData() instanceof List) {
                    List<Appointment> appointments = (List<Appointment>) response.getData();

                    if (appointments.isEmpty()) {
                        statusLabel.setForeground(ERROR_COLOR);
                        statusLabel.setText("⚠ No appointment history found for Patient ID: " + patientId);
                    } else {
                        setStatus(statusLabel, response);
                        for (Appointment a : appointments) {
                            tableModel.addRow(new Object[]{
                                a.getAppointmentId(),
                                a.getDoctorName(),
                                a.getAppointmentDate(),
                                a.getStatus()
                            });
                        }
                    }
                } else {
                    setStatus(statusLabel, response);
                }
            } catch (Exception ex) {
                setErrorStatus(statusLabel, "Error retrieving history: " + ex.getMessage());
            }
        });

        JPanel content = new JPanel(new BorderLayout(10, 15));
        content.setOpaque(false);
        content.add(formPanel, BorderLayout.NORTH);
        content.add(new JScrollPane(historyTable), BorderLayout.CENTER);

        return createCardPanel("Patient Appointment History", content, true);
    }

    // ---------- Tab 4: Update Personal Information ----------
    private JPanel buildUpdateInfoTab() {
        JTextField patientIdField = createStyledTextField();
        JTextField contactNumberField = createStyledTextField();
        JTextField icPassportField = createStyledTextField();
        JLabel statusLabel = createStatusLabel();
        JButton updateButton = createPrimaryButton("Update Info");

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = defaultGbc();
        addFormRow(formPanel, gbc, 0, "Patient ID:", patientIdField);
        addFormRow(formPanel, gbc, 1, "New Contact Number:", contactNumberField);
        addFormRow(formPanel, gbc, 2, "New IC/Passport Number:", icPassportField);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.insets = new Insets(15, 8, 5, 8);
        formPanel.add(updateButton, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(5, 8, 5, 8);
        formPanel.add(statusLabel, gbc);

        updateButton.addActionListener(e -> {
            try {
                int patientId = Integer.parseInt(patientIdField.getText().trim());
                String contactNumber = contactNumberField.getText().trim();
                String icPassport = icPassportField.getText().trim();

                Patient patient = new Patient(
                        patientId, "", "", icPassport, contactNumber, "");

                Request request = new Request(Operation.UPDATE_PATIENT_INFO, patient, username);
                Response response = clinic.processRequest(request);
                setStatus(statusLabel, response);
            } catch (Exception ex) {
                setErrorStatus(statusLabel, "Error updating info: " + ex.getMessage());
            }
        });

        return createCardPanel("Update Personal Records", formPanel, false);
    }

    // ---------- Tab 5: Check Doctor Availability ----------
    @SuppressWarnings("unchecked")
    private JPanel buildCheckAvailabilityTab() {
        JTextField doctorIdField = createStyledTextField();
        JLabel statusLabel = createStatusLabel();
        JButton checkButton = createPrimaryButton("Check Availability");

        String[] columns = {"Schedule ID", "Available Date", "Time Slot", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable availabilityTable = createStyledTable(tableModel);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = defaultGbc();
        addFormRow(formPanel, gbc, 0, "Doctor ID:", doctorIdField);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 8, 5, 8);
        formPanel.add(checkButton, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(5, 8, 5, 8);
        formPanel.add(statusLabel, gbc);

        checkButton.addActionListener(e -> {
            tableModel.setRowCount(0); // Clear existing rows
            try {
                int doctorId = Integer.parseInt(doctorIdField.getText().trim());

                Request request = new Request(Operation.CHECK_DOCTOR_AVAILABILITY, doctorId, username);
                Response response = clinic.processRequest(request);

                if (response.isSuccess() && response.getData() instanceof List) {
                    List<DoctorSchedule> schedules = (List<DoctorSchedule>) response.getData();

                    if (schedules.isEmpty()) {
                        // Show clear warning if no schedules are found
                        statusLabel.setForeground(ERROR_COLOR);
                        statusLabel.setText("⚠ No availability records found for Doctor ID: " + doctorId);
                    } else {
                        setStatus(statusLabel, response);
                        for (DoctorSchedule s : schedules) {
                            tableModel.addRow(new Object[]{
                                s.getScheduleId(),
                                s.getAvailableDate(),
                                s.getAvailableTime(),
                                s.getStatus()
                            });
                        }
                    }
                } else {
                    setStatus(statusLabel, response);
                }
            } catch (Exception ex) {
                setErrorStatus(statusLabel, "Error checking availability: " + ex.getMessage());
            }
        });

        JPanel content = new JPanel(new BorderLayout(10, 15));
        content.setOpaque(false);
        content.add(formPanel, BorderLayout.NORTH);
        content.add(new JScrollPane(availabilityTable), BorderLayout.CENTER);

        return createCardPanel("Doctor Schedule & Availability", content, true);
    }

    // ---------- UI Helper Factory Methods ----------
    private JTextField createStyledTextField() {
        JTextField field = new JTextField(18);
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
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 18, 8, 18));
        return btn;
    }

    private JButton createDangerButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BTN);
        btn.setBackground(new Color(225, 29, 72));
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 18, 8, 18));
        return btn;
    }

    private JLabel createStatusLabel() {
        JLabel label = new JLabel(" ", SwingConstants.LEFT);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        return label;
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(FONT_INPUT);
        table.setRowHeight(28);
        table.setGridColor(new Color(226, 232, 240));
        table.getTableHeader().setFont(FONT_LABEL);
        table.getTableHeader().setBackground(new Color(241, 245, 249));
        table.getTableHeader().setForeground(TEXT_DARK);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);

        return table;
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

    private void setStatus(JLabel statusLabel, Response response) {
        if (response.isSuccess()) {
            statusLabel.setForeground(SUCCESS_COLOR);
            statusLabel.setText("✔  " + response.getMessage());
        } else {
            statusLabel.setForeground(ERROR_COLOR);
            statusLabel.setText("✖  " + response.getMessage());
        }
    }

    private void setErrorStatus(JLabel statusLabel, String message) {
        statusLabel.setForeground(ERROR_COLOR);
        statusLabel.setText("✖  " + message);
    }

    private JPanel createCardPanel(String titleText, JComponent contentComponent, boolean fillSpace) {
        JPanel card = new JPanel(new BorderLayout(15, 15));
        card.setBackground(CARD_BG);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(20, 25, 20, 25)
        ));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(PRIMARY_COLOR);
        title.setBorder(new EmptyBorder(0, 0, 10, 0));

        card.add(title, BorderLayout.NORTH);
        card.add(contentComponent, BorderLayout.CENTER);

        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG_COLOR);
        outer.setBorder(new EmptyBorder(10, 10, 10, 10));

        if (fillSpace) {
            outer.add(card, BorderLayout.CENTER);
        } else {
            outer.add(card, BorderLayout.NORTH);
        }

        return outer;
    }

    public static void main(String[] args) {
        SslConfig.configureClient();
        SwingUtilities.invokeLater(() -> new PatientMainFrame().setVisible(true));
    }
}
