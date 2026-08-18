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
import model.ReportRequestData;
import service.ReportService;
import model.DoctorSchedule;
import service.DoctorService;

public class ClinicRemoteImpl extends UnicastRemoteObject implements ClinicRemote {

    private final AppointmentService appointmentService;
    private final PatientService patientService;
    private final ConsultationService consultationService;
    private final LoginService loginService;
    private final ReportService reportService;
    private final DoctorService doctorService;

    public ClinicRemoteImpl() throws RemoteException {
        super();
        appointmentService = new AppointmentService();
        patientService = new PatientService();
        consultationService = new ConsultationService();
        loginService = new LoginService();
        reportService = new ReportService();
        doctorService = new DoctorService();
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

            // Look up the real role from the USERS table instead of
            // guessing it from the username string.
            String username = request.getUsername();

            dao.UserDAO userDAO = new dao.UserDAO();
            model.User currentUser = userDAO.getUserByUsername(username);

            if (currentUser == null) {
                return new Response(false,
                        "Unknown user.",
                        null);
            }

            String role = currentUser.getRole();

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
            case Operation.CANCEL_APPOINTMENT:

                if (!(request.getData() instanceof Integer)) {
                    return new Response(false, "Invalid appointment ID.", null);
                }

                int appointmentIdToCancel = (Integer) request.getData();

                return appointmentService.cancelAppointment(appointmentIdToCancel);

            case Operation.VIEW_APPOINTMENT_HISTORY:

                if (!(request.getData() instanceof Integer)) {
                    return new Response(false, "Invalid patient ID.", null);
                }

                int patientIdForApptHistory = (Integer) request.getData();

                return appointmentService.viewAppointmentHistory(patientIdForApptHistory);

            case Operation.CHECK_DOCTOR_AVAILABILITY:

                if (!(request.getData() instanceof Integer)) {
                    return new Response(false, "Invalid doctor ID.", null);
                }

                int doctorIdForAvailability = (Integer) request.getData();

                return appointmentService.checkDoctorAvailability(doctorIdForAvailability);

            case Operation.UPDATE_PATIENT_INFO:

                if (!(request.getData() instanceof Patient)) {
                    return new Response(false, "Invalid patient data.", null);
                }

                Patient patientToUpdate = (Patient) request.getData();

                return patientService.updatePatientInfo(patientToUpdate);

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

            case Operation.GET_APPOINTMENT_SUMMARY_REPORT:

                if (!(request.getData() instanceof ReportRequestData)) {
                    return new Response(false,
                            "Invalid report request data.",
                            null);
                }

                ReportRequestData summaryParams
                        = (ReportRequestData) request.getData();

                return reportService.generateAppointmentSummaryReport(
                        summaryParams.getMonth(),
                        summaryParams.getYear());

            case Operation.GET_DOCTOR_ACTIVITY_REPORT:

                return reportService.generateDoctorActivityReport();

            case Operation.GET_PATIENT_ANALYTICS_REPORT:

                return reportService.generatePatientAnalyticsReport();

            case Operation.GET_APPOINTMENT_LIST:

                if (!(request.getData() instanceof Integer)) {
                    return new Response(false,
                            "Invalid doctor ID.",
                            null);
                }

                int doctorIdForAppointments
                        = (Integer) request.getData();

                return doctorService.getAppointmentList(doctorIdForAppointments);

            case Operation.VIEW_MEDICAL_HISTORY:

                if (!(request.getData() instanceof Integer)) {
                    return new Response(false,
                            "Invalid patient ID.",
                            null);
                }

                int patientIdForHistory
                        = (Integer) request.getData();

                return doctorService.getPatientHistory(patientIdForHistory);

            case Operation.GET_SCHEDULE:

                if (!(request.getData() instanceof Integer)) {
                    return new Response(false,
                            "Invalid doctor ID.",
                            null);
                }

                int doctorIdForSchedule
                        = (Integer) request.getData();

                return doctorService.getSchedule(doctorIdForSchedule);

            case Operation.UPDATE_SCHEDULE:

                if (!(request.getData() instanceof DoctorSchedule)) {
                    return new Response(false,
                            "Invalid schedule data.",
                            null);
                }

                DoctorSchedule scheduleToUpdate
                        = (DoctorSchedule) request.getData();

                return doctorService.updateSchedule(scheduleToUpdate);

            case Operation.VIEW_CONSULTATION:

                if (!(request.getData() instanceof Integer)) {
                    return new Response(false,
                            "Invalid consultation ID.",
                            null);
                }

                int consultationIdToView
                        = (Integer) request.getData();

                return consultationService.getConsultation(consultationIdToView);

            case Operation.ADD_CONSULTATION:

                if (!(request.getData() instanceof Consultation)) {
                    return new Response(false,
                            "Invalid consultation data.",
                            null);
                }

                Consultation newConsultation
                        = (Consultation) request.getData();

                return consultationService.addConsultation(newConsultation);

            case Operation.COMPLETE_APPOINTMENT:

                if (!(request.getData() instanceof Integer)) {
                    return new Response(false,
                            "Invalid appointment ID.",
                            null);
                }

                int appointmentIdToComplete
                        = (Integer) request.getData();

                return appointmentService.completeAppointment(appointmentIdToComplete);

            default:
                return new Response(
                        false,
                        "Unknown operation.",
                        null);

        }
    }
}
