package server;

import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import rmi.ClinicRemoteImpl;

public class RMIServer {

    public static void startServer() {

        try {

            Registry registry = LocateRegistry.createRegistry(1099);

            registry.rebind("ClinicService", (Remote) new ClinicRemoteImpl());

            System.out.println("==================================");
            System.out.println("BrightCare RMI Server Started");
            System.out.println("Port : 1099");
            System.out.println("Service : ClinicService");
            System.out.println("==================================");

        } catch (Exception e) {

            System.out.println("Server Error : " + e.getMessage());

        }

    }

}