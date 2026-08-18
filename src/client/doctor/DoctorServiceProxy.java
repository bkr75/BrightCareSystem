package client.doctor;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import model.Consultation;
import model.DoctorSchedule;
import model.LoginData;
import rmi.ClinicRemote;
import shared.Operation;
import shared.Request;
import shared.Response;

public class DoctorServiceProxy {

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 1099;
    private static final String SERVICE_NAME = "ClinicService";

    private static final int MAX_ATTEMPTS = 3;
    private static final long INITIAL_BACKOFF_MS = 1000;

    private final String serverHost;
    private final int serverPort;

    // Set once login() succeeds; every later request is stamped with it so
    // ClinicRemoteImpl can look the role up from the USERS table.
    private String username;

    public DoctorServiceProxy() {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }

    // Package-visible constructor so tests can point the proxy at a port
    // with no server listening, to exercise the retry/backoff path on demand.
    DoctorServiceProxy(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    // Optional listener the GUI can register to show connection status
    // while a retry is in progress ("Reconnecting... attempt 2/3").
    public interface RetryListener {
        void onRetry(int attempt, int maxAttempts, long backoffMillis);
    }

    private RetryListener retryListener;

    public void setRetryListener(RetryListener retryListener) {
        this.retryListener = retryListener;
    }

    // Single choke point for every RMI call made by the Doctor module.
    // Retries up to MAX_ATTEMPTS times with exponential backoff (1s, 2s, 4s)
    // when the failure looks like a connection problem. A response that
    // actually came back from the server (even success=false) is never
    // retried - that's a real answer, not a fault.
    public Response send(Request request) {

        long backoff = INITIAL_BACKOFF_MS;

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {

            try {

                Registry registry =
                        LocateRegistry.getRegistry(serverHost, serverPort);

                ClinicRemote clinic =
                        (ClinicRemote) registry.lookup(SERVICE_NAME);

                return clinic.processRequest(request);

            } catch (Exception e) {

                boolean lastAttempt = (attempt == MAX_ATTEMPTS);

                if (lastAttempt) {
                    return new Response(false,
                            "Could not reach the server after " + MAX_ATTEMPTS
                                    + " attempts: " + e.getMessage(),
                            null);
                }

                if (retryListener != null) {
                    retryListener.onRetry(attempt, MAX_ATTEMPTS, backoff);
                }

                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                    return new Response(false, "Retry interrupted.", null);
                }

                backoff *= 2;
            }
        }

        // Unreachable, but the compiler needs a return on every path.
        return new Response(false, "Unexpected error.", null);
    }

    public Response login(String username, String password) {

        Request request = new Request(
                Operation.LOGIN,
                new LoginData(username, password),
                username);

        Response response = send(request);

        if (response.isSuccess()) {
            this.username = username;
        }

        return response;
    }

    public Response getAppointmentList(int doctorId) {
        return send(new Request(Operation.GET_APPOINTMENT_LIST, doctorId, username));
    }

    public Response getPatientHistory(int patientId) {
        return send(new Request(Operation.VIEW_MEDICAL_HISTORY, patientId, username));
    }

    public Response getSchedule(int doctorId) {
        return send(new Request(Operation.GET_SCHEDULE, doctorId, username));
    }

    public Response updateSchedule(DoctorSchedule schedule) {
        return send(new Request(Operation.UPDATE_SCHEDULE, schedule, username));
    }

    public Response getConsultation(int consultationId) {
        return send(new Request(Operation.VIEW_CONSULTATION, consultationId, username));
    }

    public Response updateConsultationNotes(Consultation consultation) {
        return send(new Request(Operation.UPDATE_DIAGNOSIS, consultation, username));
    }
}
