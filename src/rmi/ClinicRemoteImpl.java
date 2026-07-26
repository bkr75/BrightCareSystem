package rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import model.Appointment;
import security.Authorization;
import service.AppointmentService;
import shared.Operation;
import shared.Request;
import shared.Response;
import model.Patient;
import service.PatientService;
import model.Consultation;
import service.ConsultationService;
import model.LoginData;
import service.LoginService;

public class ClinicRemoteImpl extends UnicastRemoteObject implements ClinicRemote {

    private final AppointmentService appointmentService;
    private final PatientService patientService;
    private final ConsultationService consultationService;
    private final LoginService loginService;

    public ClinicRemoteImpl() throws RemoteException {
        super();
        appointmentService = new AppointmentService();
        patientService = new PatientService();
        consultationService = new ConsultationService();
        loginService = new LoginService();
    }

    @Override
    public Response processRequest(Request request) throws RemoteException {

        if (request == null) {
            return new Response(false, "Invalid request.", null);
        }

        if (request.getOperation() == null) {
            return new Response(false, "Operation is missing.", null);
        }

        // LOGIN and TEST_CONNECTION do not require authorization.
        if (!Operation.LOGIN.equals(request.getOperation())
                && !Operation.TEST_CONNECTION.equals(request.getOperation())) {

            // Temporary role until real authentication is implemented.
            String role = "DOCTOR";

            if (!Authorization.hasPermission(role, request.getOperation())) {
                return new Response(false,
                        "Access denied.",
                        null);
            }
        }

        switch (request.getOperation()) {

            case Operation.TEST_CONNECTION:
                return new Response(
                        true,
                        "Connection successful.",
                        null);

            case Operation.BOOK_APPOINTMENT:

                if (!(request.getData() instanceof Appointment)) {
                    return new Response(
                            false,
                            "Invalid appointment data.",
                            null);
                }

                Appointment appointment
                        = (Appointment) request.getData();

                return appointmentService.bookAppointment(appointment);

            case Operation.REGISTER_PATIENT:

                if (!(request.getData() instanceof Patient)) {
                    return new Response(false,
                            "Invalid patient data.",
                            null);

                }

                Patient patient
                        = (Patient) request.getData();

                return patientService.registerPatient(patient);

            case Operation.VIEW_PATIENT:

                if (!(request.getData() instanceof Integer)) {
                    return new Response(false,
                            "Invalid patient ID.",
                            null);
                }

                int patientId
                        = (Integer) request.getData();

                return patientService.viewPatient(patientId);

            case Operation.UPDATE_DIAGNOSIS:

                if (!(request.getData() instanceof Consultation)) {
                    return new Response(false,
                            "Invalid consultation data.",
                            null);
                }

                Consultation consultation
                        = (Consultation) request.getData();

                return consultationService.updateDiagnosis(consultation);

            case Operation.LOGIN:

                if (!(request.getData() instanceof LoginData)) {
                    return new Response(false,
                            "Invalid login data.",
                            null);
                }

                LoginData loginData = (LoginData) request.getData();

                return loginService.login(loginData);

            default:
                return new Response(
                        false,
                        "Unknown operation.",
                        null);

        }
    }
}
