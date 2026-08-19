package client.admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

import model.AppointmentSummaryReport;
import model.DoctorActivityReport;
import model.PatientAnalyticsReport;
import shared.Response;
import security.SslConfig;

public class AdminMainFrame extends JFrame {

    private final AdminServiceProxy proxy = new AdminServiceProxy();

    public AdminMainFrame() {

        super("BrightCare Medical Center - Admin Console");

        AdminTheme.applySystemLookAndFeel();

        setSize(850, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        showLoginScreen();
    }

    // ------------------------------------------------------------------
    // Login screen
    // ------------------------------------------------------------------
    private void showLoginScreen() {

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(AdminTheme.BG_COLOR);

        JTextField usernameField = AdminTheme.createStyledTextField();
        JPasswordField passwordField = AdminTheme.createStyledPasswordField();
        JLabel statusLabel = AdminTheme.createStatusLabel();

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = AdminTheme.defaultGbc();
        AdminTheme.addFormRow(form, gbc, 0, "Username:", usernameField);
        AdminTheme.addFormRow(form, gbc, 1, "Password:", passwordField);

        JButton loginBtn = AdminTheme.createPrimaryButton("Log In");
        gbc.gridx = 1;
        gbc.gridy = 2;
        form.add(loginBtn, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        form.add(statusLabel, gbc);
        form.setBackground(AdminTheme.CARD_BG);

        JPanel card = (JPanel) AdminTheme.createCardPanel("Admin Login", form, false);

        wrapper.add(card);

        loginBtn.addActionListener(e -> {

            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                AdminTheme.setErrorStatus(statusLabel, "Enter both username and password.");
                return;
            }

            Response response = proxy.login(username, password);

            if (response.isSuccess()) {
                showDashboard();
            } else {
                AdminTheme.setErrorStatus(statusLabel, response.getMessage());
            }
        });

        setContentPane(wrapper);
        revalidate();
        repaint();
    }

    // ------------------------------------------------------------------
    // Main dashboard (shown after a successful login)
    // ------------------------------------------------------------------
    private void showDashboard() {

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(AdminTheme.BG_COLOR);

        JLabel serverStatusLabel = new JLabel();
        AdminTheme.setServerStatus(serverStatusLabel, proxy.isServerReachable());
        mainContainer.add(
                AdminTheme.createHeaderPanel("Admin Reports Console", serverStatusLabel),
                BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(AdminTheme.FONT_LABEL);
        tabbedPane.addTab("  Appointment Summary  ", buildAppointmentSummaryPanel());
        tabbedPane.addTab("  Doctor Activity  ", buildDoctorActivityPanel());
        tabbedPane.addTab("  Patient Analytics  ", buildPatientAnalyticsPanel());

        JPanel tabWrapper = new JPanel(new BorderLayout());
        tabWrapper.setBackground(AdminTheme.BG_COLOR);
        tabWrapper.setBorder(new EmptyBorder(15, 15, 15, 15));
        tabWrapper.add(tabbedPane, BorderLayout.CENTER);

        mainContainer.add(tabWrapper, BorderLayout.CENTER);

        setContentPane(mainContainer);
        revalidate();
        repaint();
    }

    // ------------------------------------------------------------------
    // Tab 1: Monthly Appointment Summary Report
    // ------------------------------------------------------------------
    private JPanel buildAppointmentSummaryPanel() {

        JTextField monthField = AdminTheme.createStyledTextField();
        JTextField yearField = AdminTheme.createStyledTextField();
        JLabel statusLabel = AdminTheme.createStatusLabel();

        JTextArea resultArea = new JTextArea(8, 40);
        resultArea.setEditable(false);
        resultArea.setFont(AdminTheme.FONT_INPUT);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(AdminTheme.CARD_BG);
        GridBagConstraints gbc = AdminTheme.defaultGbc();
        AdminTheme.addFormRow(form, gbc, 0, "Month (1-12):", monthField);
        AdminTheme.addFormRow(form, gbc, 1, "Year (e.g. 2026):", yearField);

        JButton runBtn = AdminTheme.createPrimaryButton("Generate Report");
        gbc.gridx = 1;
        gbc.gridy = 2;
        form.add(runBtn, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        form.add(new JScrollPane(resultArea), gbc);

        gbc.gridy = 4;
        form.add(statusLabel, gbc);

        runBtn.addActionListener(e -> {

            int month;
            int year;

            try {
                month = Integer.parseInt(monthField.getText().trim());
                year = Integer.parseInt(yearField.getText().trim());
            } catch (NumberFormatException ex) {
                AdminTheme.setErrorStatus(statusLabel, "Enter valid numbers for month and year.");
                return;
            }

            Response response = proxy.getAppointmentSummaryReport(month, year);

            if (!response.isSuccess()) {
                AdminTheme.setErrorStatus(statusLabel, response.getMessage());
                resultArea.setText("");
                return;
            }

            AppointmentSummaryReport report = (AppointmentSummaryReport) response.getData();

            resultArea.setText(
                    "Period               : " + report.getMonth() + "/" + report.getYear() + "\n"
                    + "Total appointments   : " + report.getTotalAppointments() + "\n"
                    + "Completed            : " + report.getCompletedCount() + "\n"
                    + "Cancelled            : " + report.getCancelledCount() + "\n"
                    + "Pending/other        : " + report.getPendingCount());

            AdminTheme.setSuccessStatus(statusLabel, "Report generated.");
        });

        return AdminTheme.createCardPanel("Monthly Appointment Summary", form, true);
    }

    // ------------------------------------------------------------------
    // Tab 2: Doctor Activity Report
    // ------------------------------------------------------------------
    private JPanel buildDoctorActivityPanel() {

        DefaultTableModel tableModel = new DefaultTableModel(
                new Object[]{"Doctor", "Specialization", "Appointments"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = AdminTheme.createStyledTable(tableModel);
        JLabel statusLabel = AdminTheme.createStatusLabel();

        JButton runBtn = AdminTheme.createPrimaryButton("Load Doctor Activity");

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBackground(AdminTheme.CARD_BG);

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.setBackground(AdminTheme.CARD_BG);
        topBar.add(runBtn);
        topBar.add(statusLabel);

        content.add(topBar, BorderLayout.NORTH);
        content.add(new JScrollPane(table), BorderLayout.CENTER);

        runBtn.addActionListener(e -> {

            Response response = proxy.getDoctorActivityReport();

            if (!response.isSuccess()) {
                AdminTheme.setErrorStatus(statusLabel, response.getMessage());
                return;
            }

            @SuppressWarnings("unchecked")
            List<DoctorActivityReport> reports = (List<DoctorActivityReport>) response.getData();

            tableModel.setRowCount(0);

            for (DoctorActivityReport report : reports) {
                tableModel.addRow(new Object[]{
                    report.getDoctorName(),
                    report.getSpecialization(),
                    report.getTotalAppointments()
                });
            }

            AdminTheme.setSuccessStatus(statusLabel,
                    reports.isEmpty() ? "No doctors found." : "Report loaded.");
        });

        return AdminTheme.createCardPanel("Doctor Activity Report", content, true);
    }

    // ------------------------------------------------------------------
    // Tab 3: Patient Analytics Report
    // ------------------------------------------------------------------
    private JPanel buildPatientAnalyticsPanel() {

        JTextArea resultArea = new JTextArea(8, 40);
        resultArea.setEditable(false);
        resultArea.setFont(AdminTheme.FONT_INPUT);

        JLabel statusLabel = AdminTheme.createStatusLabel();
        JButton runBtn = AdminTheme.createPrimaryButton("Load Patient Analytics");

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBackground(AdminTheme.CARD_BG);

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.setBackground(AdminTheme.CARD_BG);
        topBar.add(runBtn);
        topBar.add(statusLabel);

        content.add(topBar, BorderLayout.NORTH);
        content.add(new JScrollPane(resultArea), BorderLayout.CENTER);

        runBtn.addActionListener(e -> {

            Response response = proxy.getPatientAnalyticsReport();

            if (!response.isSuccess()) {
                AdminTheme.setErrorStatus(statusLabel, response.getMessage());
                resultArea.setText("");
                return;
            }

            PatientAnalyticsReport report = (PatientAnalyticsReport) response.getData();

            resultArea.setText(
                    "Total patients             : " + report.getTotalPatients() + "\n"
                    + "Total appointments          : " + report.getTotalAppointments() + "\n"
                    + String.format("Avg appointments/patient    : %.2f",
                            report.getAverageAppointmentsPerPatient()));

            AdminTheme.setSuccessStatus(statusLabel, "Report loaded.");
        });

        return AdminTheme.createCardPanel("Patient Analytics Report", content, true);
    }

    public static void main(String[] args) {
        SslConfig.configureClient();
        SwingUtilities.invokeLater(() -> new AdminMainFrame().setVisible(true));
    }
}
