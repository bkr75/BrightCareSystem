package client.receptionist;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import model.Patient;
import rmi.ClinicRemote;
import shared.Operation;
import shared.Request;
import shared.Response;
/**
 * Command-Line client for the Receptionist role.
 *
 * Feature covered: Patient Registration.
 *
 * Talks to the server only through ClinicRemote.processRequest(Request),
 * following the same Request/Response/Operation pattern used across the
 * whole system (see rmi.ClinicRemoteImpl).
 */
public class ReceptionistClient {
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
            System.out.println("\n===== RECEPTIONIST MENU =====");
            System.out.println("1. Register New Patient");
            System.out.println("2. Exit");
            System.out.print("Choose an option: ");
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1":
                    registerPatient();
                    break;
                case "2":
                    running = false;
                    System.out.println("Goodbye.");
                    break;
                default:
                    System.out.println("Invalid option, please try again.");
            }
        }
    }
    private static void registerPatient() {
        try {
            System.out.println("\n--- Register New Patient ---");
            System.out.print("First Name: ");
            String firstName = sc.nextLine().trim();
            System.out.print("Last Name: ");
            String lastName = sc.nextLine().trim();
            System.out.print("IC/Passport Number: ");
            String icPassport = sc.nextLine().trim();
            System.out.print("Contact Number: ");
            String contactNumber = sc.nextLine().trim();
            System.out.print("Medical Record ID: ");
            String medicalRecordId = sc.nextLine().trim();
            Patient patient = new Patient(
                    firstName,
                    lastName,
                    icPassport,
                    contactNumber,
                    medicalRecordId
            );
            Request request = new Request(Operation.REGISTER_PATIENT, patient, "receptionist");
            Response response = clinic.processRequest(request);
            System.out.println("Success : " + response.isSuccess());
            System.out.println("Message : " + response.getMessage());
        } catch (Exception e) {
            System.out.println("Error registering patient: " + e.getMessage());
        }
    }
}