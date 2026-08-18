package client.patient;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Date;
import java.util.List;
import java.util.Scanner;

import model.Appointment;
import model.DoctorSchedule;
import model.Patient;
import rmi.ClinicRemote;
import shared.Operation;
import shared.Request;
import shared.Response;

/**
 * Command-Line client for the Patient role.
 *
 * Features covered: Book Appointment, Cancel Appointment, View Appointment
 * History, Update Personal Information, Check Doctor Availability.
 *
 * Talks to the server only through ClinicRemote.processRequest(Request),
 * following the same Request/Response/Operation pattern used across the whole
 * system (see rmi.ClinicRemoteImpl).
 */
public class PatientClient {

    private static ClinicRemote clinic;
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            clinic = (ClinicRemote) registry.lookup("ClinicService");

            System.out.println("Connected to BrightCare Clinic Server.");
            showMenu();

        } catch (Exception e) {
            System.out.println("Client Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void showMenu() {

        boolean running = true;

        while (running) {

            System.out.println("\n===== PATIENT MENU =====");
            System.out.println("1. Book Appointment");
            System.out.println("2. Cancel Appointment");
            System.out.println("3. View Appointment History");
            System.out.println("4. Update Personal Information");
            System.out.println("5. Check Doctor Availability");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");

            String choice = sc.nextLine().trim();

            switch (choice) {

                case "1":
                    bookAppointment();
                    break;

                case "2":
                    cancelAppointment();
                    break;

                case "3":
                    viewAppointmentHistory();
                    break;

                case "4":
                    updatePersonalInfo();
                    break;

                case "5":
                    checkDoctorAvailability();
                    break;

                case "6":
                    running = false;
                    System.out.println("Goodbye.");
                    break;

                default:
                    System.out.println("Invalid option, please try again.");
            }
        }
    }

    private static void bookAppointment() {

        try {
            System.out.println("\n--- Book Appointment ---");

            System.out.print("Patient ID: ");
            int patientId = Integer.parseInt(sc.nextLine().trim());

            System.out.print("Doctor ID: ");
            int doctorId = Integer.parseInt(sc.nextLine().trim());

            System.out.print("Schedule ID: ");
            int scheduleId = Integer.parseInt(sc.nextLine().trim());

            System.out.print("Appointment Date (YYYY-MM-DD): ");
            Date appointmentDate = Date.valueOf(sc.nextLine().trim());

            Appointment appointment = new Appointment(
                    patientId,
                    doctorId,
                    scheduleId,
                    appointmentDate,
                    "BOOKED"
            );

            Request request = new Request(Operation.BOOK_APPOINTMENT, appointment, "patient");
            Response response = clinic.processRequest(request);

            //System.out.println("Success : " + response.isSuccess());
            System.out.println(response.getMessage());

        } catch (Exception e) {
            System.out.println("Error booking appointment: " + e.getMessage());
        }
    }

    private static void cancelAppointment() {

        try {
            System.out.println("\n--- Cancel Appointment ---");

            System.out.print("Appointment ID: ");
            int appointmentId = Integer.parseInt(sc.nextLine().trim());

            Request request = new Request(Operation.CANCEL_APPOINTMENT, appointmentId, "patient");
            Response response = clinic.processRequest(request);

            //System.out.println("Success : " + response.isSuccess());
            System.out.println(response.getMessage());

        } catch (Exception e) {
            System.out.println("Error cancelling appointment: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static void viewAppointmentHistory() {

        try {
            System.out.println("\n--- View Appointment History ---");

            System.out.print("Patient ID: ");
            int patientId = Integer.parseInt(sc.nextLine().trim());

            Request request = new Request(Operation.VIEW_APPOINTMENT_HISTORY, patientId, "patient");
            Response response = clinic.processRequest(request);

            //System.out.println("Success : " + response.isSuccess());
            System.out.println(response.getMessage());

            if (response.isSuccess() && response.getData() instanceof List) {

                List<Appointment> appointments = (List<Appointment>) response.getData();

                if (appointments.isEmpty()) {
                    System.out.println("No appointments found.");
                }

                for (Appointment a : appointments) {
                    System.out.println(
                            "Appointment #" + a.getAppointmentId()
                            + " | Doctor: " + a.getDoctorName()
                            + " | Date: " + a.getAppointmentDate()
                            + " | Status: " + a.getStatus());
                }
            }

        } catch (Exception e) {
            System.out.println("Error retrieving appointment history: " + e.getMessage());
        }
    }

    private static void updatePersonalInfo() {

        try {
            System.out.println("\n--- Update Personal Information ---");

            System.out.print("Patient ID: ");
            int patientId = Integer.parseInt(sc.nextLine().trim());

            System.out.print("New Contact Number: ");
            String contactNumber = sc.nextLine().trim();

            System.out.print("New IC/Passport Number: ");
            String icPassport = sc.nextLine().trim();

            // Only contactNumber and icPassport are used by PatientDAO.updatePatient(...)
            Patient patient = new Patient(
                    patientId,
                    "",
                    "",
                    icPassport,
                    contactNumber,
                    ""
            );

            Request request = new Request(Operation.UPDATE_PATIENT_INFO, patient, "patient");
            Response response = clinic.processRequest(request);

            // System.out.println("Success : " + response.isSuccess());
            System.out.println(response.getMessage());

        } catch (Exception e) {
            System.out.println("Error updating patient info: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static void checkDoctorAvailability() {

        try {
            System.out.println("\n--- Check Doctor Availability ---");

            System.out.print("Doctor ID: ");
            int doctorId = Integer.parseInt(sc.nextLine().trim());

            Request request = new Request(Operation.CHECK_DOCTOR_AVAILABILITY, doctorId, "patient");
            Response response = clinic.processRequest(request);

            //System.out.println("Success : " + response.isSuccess());
            System.out.println(response.getMessage());

            if (response.isSuccess() && response.getData() instanceof List) {

                List<DoctorSchedule> schedules = (List<DoctorSchedule>) response.getData();

                if (schedules.isEmpty()) {
                    System.out.println("No available slots found.");
                }

                for (DoctorSchedule s : schedules) {
                    System.out.println(
                            "Schedule #" + s.getScheduleId()
                            + " | Date: " + s.getAvailableDate()
                            + " | Time: " + s.getAvailableTime()
                            + " | Status: " + s.getStatus());
                }
            }

        } catch (Exception e) {
            System.out.println("Error checking doctor availability: " + e.getMessage());
        }
    }
}
