package client.doctor;

import shared.Response;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

// Simple modal login screen shown before the Doctor portal opens.
// A successful login stamps the proxy with the username so every
// later request can be authorized against the USERS table.
public class DoctorLoginDialog extends JDialog {

    private final DoctorServiceProxy proxy;
    private boolean loggedIn = false;

    public DoctorLoginDialog(DoctorServiceProxy proxy) {

        super((Frame) null, "BrightCare Medical Center - Doctor Login", true);
        this.proxy = proxy;

        DoctorTheme.applySystemLookAndFeel();

        JTextField usernameField = DoctorTheme.createStyledTextField();
        JPasswordField passwordField = new JPasswordField(18);
        passwordField.setFont(DoctorTheme.FONT_INPUT);
        passwordField.setBorder(usernameField.getBorder());

        JLabel statusLabel = DoctorTheme.createStatusLabel();
        JButton loginButton = DoctorTheme.createPrimaryButton("Login");

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = DoctorTheme.defaultGbc();
        DoctorTheme.addFormRow(formPanel, gbc, 0, "Username:", usernameField);
        DoctorTheme.addFormRow(formPanel, gbc, 1, "Password:", passwordField);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.insets = new Insets(15, 8, 5, 8);
        formPanel.add(loginButton, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(5, 8, 5, 8);
        formPanel.add(statusLabel, gbc);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(DoctorTheme.BG_COLOR);
        content.setBorder(new EmptyBorder(15, 15, 15, 15));
        content.add(DoctorTheme.createCardPanel("Doctor Login", formPanel, false), BorderLayout.CENTER);

        setContentPane(content);

        Runnable attemptLogin = () -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                DoctorTheme.setErrorStatus(statusLabel, "Please enter both username and password.");
                return;
            }

            Response response = proxy.login(username, password);
            DoctorTheme.setStatus(statusLabel, response);

            if (response.isSuccess()) {
                loggedIn = true;
                dispose();
            }
        };

        loginButton.addActionListener(e -> attemptLogin.run());
        passwordField.addActionListener(e -> attemptLogin.run());

        setSize(420, 280);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }
}
