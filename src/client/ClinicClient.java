package client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import model.LoginData;
import rmi.ClinicRemote;
import shared.Operation;
import shared.Request;
import shared.Response;

public class ClinicClient {

    public static void main(String[] args) {

        try {

            Registry registry = LocateRegistry.getRegistry("localhost", 1099);

            ClinicRemote clinic
                    = (ClinicRemote) registry.lookup("ClinicService");

            LoginData loginData = new LoginData(
                    "admin",
                    "1111"
            );

            Request request = new Request(
                    Operation.LOGIN,
                    loginData
            );

            // Send request to server
            Response response = clinic.processRequest(request);

            // Display result
            System.out.println("Success : " + response.isSuccess());
            System.out.println("Message : " + response.getMessage());

        } catch (Exception e) {

            System.out.println("Client Error: " + e.getMessage());
            e.printStackTrace();

        }

    }

}
