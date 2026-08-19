package client.admin;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import security.SslNoHostnameCheckSocketFactory;

import model.LoginData;
import model.ReportRequestData;
import rmi.ClinicRemote;
import shared.Operation;
import shared.Request;
import shared.Response;

public class AdminServiceProxy {

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 1099;
    private static final String SERVICE_NAME = "ClinicService";

    private final String serverHost;
    private final int serverPort;

    // Set once login() succeeds; every later request is stamped with it so
    // ClinicRemoteImpl can look the role up from the USERS table.
    private String username;

    public AdminServiceProxy() {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }

    public AdminServiceProxy(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public boolean isServerReachable() {
        try {
            Registry registry = LocateRegistry.getRegistry(
                    serverHost, serverPort, new SslNoHostnameCheckSocketFactory());
            return registry.lookup(SERVICE_NAME) instanceof ClinicRemote;
        } catch (Exception e) {
            return false;
        }
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

    public Response getAppointmentSummaryReport(int month, int year) {
        return send(new Request(
                Operation.GET_APPOINTMENT_SUMMARY_REPORT,
                new ReportRequestData(month, year),
                username));
    }

    public Response getDoctorActivityReport() {
        return send(new Request(
                Operation.GET_DOCTOR_ACTIVITY_REPORT,
                null,
                username));
    }

    public Response getPatientAnalyticsReport() {
        return send(new Request(
                Operation.GET_PATIENT_ANALYTICS_REPORT,
                null,
                username));
    }

    private Response send(Request request) {
        try {
            Registry registry = LocateRegistry.getRegistry(
                    serverHost, serverPort, new SslNoHostnameCheckSocketFactory());
            ClinicRemote clinic = (ClinicRemote) registry.lookup(SERVICE_NAME);
            return clinic.processRequest(request);
        } catch (Exception e) {
            return new Response(false,
                    "Could not reach the server: " + e.getMessage(),
                    null);
        }
    }
}
