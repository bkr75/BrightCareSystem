package client.doctor;

import javax.swing.*;
import java.awt.*;

import shared.Response;

// The Doctor login screen, extracted out of DoctorMainFrame purely to keep
// that file smaller and easier to read. This is a plain JPanel - NOT a
// separate window - so DoctorMainFrame just swaps it into its own content
// pane. There is still only ONE frame/window at runtime.
public class DoctorLoginPanel extends JPanel {

    // Called once login succeeds, so DoctorMainFrame can switch to the portal.
    public interface LoginListener {

        void onLoginSuccess();
    }

    public DoctorLoginPanel(DoctorServiceProxy proxy, LoginListener listener) {

        super(new GridBagLayout());
        setBackground(DoctorTheme.BG_COLOR);

        JTextField usernameField = DoctorTheme.createStyledTextField();
        JPasswordField passwordField = new JPasswordField(18);
        passwordField.setFont(DoctorTheme.FONT_INPUT);
        passwordField.setBorder(usernameField.getBorder());

        JLabel statusLabel = DoctorTheme.createStatusLabel();
        JButton loginButton = DoctorTheme.createPrimaryButton("Log In");

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = DoctorTheme.defaultGbc();
        DoctorTheme.addFormRow(form, gbc, 0, "Username:", usernameField);
        DoctorTheme.addFormRow(form, gbc, 1, "Password:", passwordField);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.insets = new Insets(15, 8, 5, 8);
        form.add(loginButton, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(5, 8, 5, 8);
        form.add(statusLabel, gbc);

        add(DoctorTheme.createCardPanel("Doctor Login", form, false));

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
                listener.onLoginSuccess();
            }
        };

        loginButton.addActionListener(e -> attemptLogin.run());
        passwordField.addActionListener(e -> attemptLogin.run());
    }
}