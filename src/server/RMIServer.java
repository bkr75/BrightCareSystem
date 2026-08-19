package server;

import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.rmi.ssl.SslRMIServerSocketFactory;
import rmi.ClinicRemoteImpl;
import security.SslConfig;
import security.SslNoHostnameCheckSocketFactory;

public class RMIServer {

    public static void startServer() {

        try {

            SslConfig.configureServer();

            SslNoHostnameCheckSocketFactory csf = new SslNoHostnameCheckSocketFactory();
            SslRMIServerSocketFactory ssf = new SslRMIServerSocketFactory();

            Registry registry = LocateRegistry.createRegistry(1099, csf, ssf);

            registry.rebind("ClinicService", (Remote) new ClinicRemoteImpl());

            System.out.println("==================================");
            System.out.println("BrightCare RMI Server Started (TLS)");
            System.out.println("Port : 1099");
            System.out.println("Service : ClinicService");
            System.out.println("==================================");

        } catch (Exception e) {

            System.out.println("Server Error : " + e.getMessage());
            e.printStackTrace();

        }

    }

}
