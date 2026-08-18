package client.doctor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import rmi.ClinicRemote;

public class DoctorMainFrame extends JFrame {

    public DoctorMainFrame(DoctorServiceProxy proxy) {

        super("BrightCare Medical Center - Doctor Consultation Portal");

        DoctorTheme.applySystemLookAndFeel();

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(DoctorTheme.BG_COLOR);

        JLabel serverStatusLabel = new JLabel();
        DoctorTheme.setServerStatus(serverStatusLabel, isServerReachable());
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
        setSize(850, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private boolean isServerReachable() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            return registry.lookup("ClinicService") instanceof ClinicRemote;
        } catch (Exception e) {
            return false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DoctorServiceProxy proxy = new DoctorServiceProxy();

            DoctorLoginDialog loginDialog = new DoctorLoginDialog(proxy);
            loginDialog.setVisible(true);

            if (loginDialog.isLoggedIn()) {
                new DoctorMainFrame(proxy).setVisible(true);
            }
        });
    }
}
