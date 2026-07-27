package client.doctor;

import javax.swing.*;

public class DoctorMainFrame extends JFrame {

    public DoctorMainFrame() {

        super("BrightCare - Doctor Module");

        DoctorServiceProxy proxy = new DoctorServiceProxy();

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Appointments", new AppointmentListPanel(proxy));
        tabbedPane.addTab("Consultation Notes", new ConsultationPanel(proxy));
        tabbedPane.addTab("Patient History", new PatientHistoryPanel(proxy));
        tabbedPane.addTab("Schedule", new SchedulePanel(proxy));

        setContentPane(tabbedPane);

        setSize(700, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DoctorMainFrame().setVisible(true));
    }
}
