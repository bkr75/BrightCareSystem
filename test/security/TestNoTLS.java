package security;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import rmi.ClinicRemote;
import shared.Operation;
import shared.Request;
import shared.Response;

public class TestNoTLS {
    public static void main(String[] args) {
        try {
            // Connecting WITHOUT any TLS socket factory - should be rejected
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            ClinicRemote clinic = (ClinicRemote) registry.lookup("ClinicService");
            Response r = clinic.processRequest(new Request(Operation.TEST_CONNECTION, null, "test"));
            System.out.println("UNEXPECTED SUCCESS (no TLS): " + r.getMessage());
        } catch (Exception e) {
            System.out.println("CONNECTION REJECTED (expected): " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }
}