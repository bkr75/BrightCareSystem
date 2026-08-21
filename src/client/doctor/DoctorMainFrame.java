package client.doctor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DoctorMainFrame extends JFrame {

    private final DoctorServiceProxy proxy = new DoctorServiceProxy();

    public DoctorMainFrame() {

        super("BrightCare Medical Center - Doctor Consultation Portal");

        DoctorTheme.applySystemLookAndFeel();

        setSize(850, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        showLoginScreen();
    }

    // ---------- Login screen (shown first) ----------
    private void showLoginScreen() {
        setContentPane(new DoctorLoginPanel(proxy, this::showPortal));
        revalidate();
        repaint();
    }

    // ---------- Main portal (shown after a successful login) ----------
    private void showPortal() {

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(DoctorTheme.BG_COLOR);

        JLabel serverStatusLabel = new JLabel();
        DoctorTheme.setServerStatus(serverStatusLabel, proxy.isServerReachable());
        mainContainer.add(
                DoctorTheme.createHeaderPanel("Doctor Consultation Portal", serverStatusLabel),
                BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(DoctorTheme.FONT_LABEL);
        tabbedPane.addTab("  Appointments  ", new AppointmentListPanel(proxy));
        tabbedPane.addTab("  Consultation Notes  ", new ConsultationPanel(proxy));
        tabbedPane.addTab("  Patient History  ", new PatientHistoryPanel(proxy));
        tabbedPane.addTab("  Schedule  ", new SchedulePanel(proxy));

        JPanel tabWrapper = new JPanel(new BorderLayout());
        tabWrapper.setBackground(DoctorTheme.BG_COLOR);
        tabWrapper.setBorder(new EmptyBorder(15, 15, 15, 15));
        tabWrapper.add(tabbedPane, BorderLayout.CENTER);

        mainContainer.add(tabWrapper, BorderLayout.CENTER);

        setContentPane(mainContainer);
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        security.SslConfig.configureClient();
        SwingUtilities.invokeLater(() -> new DoctorMainFrame().setVisible(true));
    }
}