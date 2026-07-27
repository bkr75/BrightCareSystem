package client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Scanner;

import model.AppointmentSummaryReport;
import model.DoctorActivityReport;
import model.LoginData;
import model.PatientAnalyticsReport;
import model.ReportRequestData;
import rmi.ClinicRemote;
import shared.Operation;
import shared.Request;
import shared.Response;

public class AdminClient {

    private static ClinicRemote clinic;
    private static String username;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            clinic = (ClinicRemote) registry.lookup("ClinicService");

        } catch (Exception e) {
            System.out.println("Could not connect to server: " + e.getMessage());
            return;
        }

        if (!login()) {
            return;
        }

        boolean running = true;

        while (running) {

            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    runAppointmentSummaryReport();
                    break;
                case "2":
                    runDoctorActivityReport();
                    break;
                case "3":
                    runPatientAnalyticsReport();
                    break;
                case "4":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option, try again.");
            }
        }

        System.out.println("Goodbye.");
    }

    private static boolean login() {

        System.out.print("Admin username: ");
        String user = scanner.nextLine().trim();

        System.out.print("Password: ");
        String pass = scanner.nextLine().trim();

        try {
            Request request = new Request(
                    Operation.LOGIN,
                    new LoginData(user, pass),
                    user);

            Response response = clinic.processRequest(request);

            System.out.println("Message : " + response.getMessage());

            if (response.isSuccess()) {
                username = user;
                return true;
            }

            return false;

        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
            return false;
        }
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("==== BrightCare Admin Console ====");
        System.out.println("1. Monthly Appointment Summary Report");
        System.out.println("2. Doctor Activity Report");
        System.out.println("3. Patient Analytics Report");
        System.out.println("4. Exit");
        System.out.print("Choose an option: ");
    }

    private static void runAppointmentSummaryReport() {

        try {
            System.out.print("Month (1-12): ");
            int month = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Year (e.g. 2026): ");
            int year = Integer.parseInt(scanner.nextLine().trim());

            Request request = new Request(
                    Operation.GET_APPOINTMENT_SUMMARY_REPORT,
                    new ReportRequestData(month, year),
                    username);

            Response response = clinic.processRequest(request);

            if (!response.isSuccess()) {
                System.out.println("Failed: " + response.getMessage());
                return;
            }

            AppointmentSummaryReport report =
                    (AppointmentSummaryReport) response.getData();

            System.out.println();
            System.out.println("--- Appointment Summary (" + report.getMonth()
                    + "/" + report.getYear() + ") ---");
            System.out.println("Total appointments : " + report.getTotalAppointments());
            System.out.println("Completed          : " + report.getCompletedCount());
            System.out.println("Cancelled          : " + report.getCancelledCount());
            System.out.println("Pending/other      : " + report.getPendingCount());

        } catch (NumberFormatException e) {
            System.out.println("Please enter valid numbers for month and year.");
        } catch (Exception e) {
            System.out.println("Error generating report: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static void runDoctorActivityReport() {

        try {
            Request request = new Request(
                    Operation.GET_DOCTOR_ACTIVITY_REPORT,
                    null,
                    username);

            Response response = clinic.processRequest(request);

            if (!response.isSuccess()) {
                System.out.println("Failed: " + response.getMessage());
                return;
            }

            List<DoctorActivityReport> reports =
                    (List<DoctorActivityReport>) response.getData();

            System.out.println();
            System.out.println("--- Doctor Activity Report ---");

            if (reports.isEmpty()) {
                System.out.println("No doctors found.");
                return;
            }

            for (DoctorActivityReport report : reports) {
                System.out.println(report.getDoctorName()
                        + " (" + report.getSpecialization() + ") - "
                        + report.getTotalAppointments() + " appointment(s)");
            }

        } catch (Exception e) {
            System.out.println("Error generating report: " + e.getMessage());
        }
    }

    private static void runPatientAnalyticsReport() {

        try {
            Request request = new Request(
                    Operation.GET_PATIENT_ANALYTICS_REPORT,
                    null,
                    username);

            Response response = clinic.processRequest(request);

            if (!response.isSuccess()) {
                System.out.println("Failed: " + response.getMessage());
                return;
            }

            PatientAnalyticsReport report =
                    (PatientAnalyticsReport) response.getData();

            System.out.println();
            System.out.println("--- Patient Analytics Report ---");
            System.out.println("Total patients            : " + report.getTotalPatients());
            System.out.println("Total appointments         : " + report.getTotalAppointments());
            System.out.printf("Avg appointments/patient   : %.2f%n",
                    report.getAverageAppointmentsPerPatient());

        } catch (Exception e) {
            System.out.println("Error generating report: " + e.getMessage());
        }
    }
}
