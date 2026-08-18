package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import shared.Request;
import shared.Response;

public interface ClinicRemote extends Remote {

    Response processRequest(Request request) throws RemoteException;

}
