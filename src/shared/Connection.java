package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Connection extends Remote {
    String getIp() throws RemoteException;
    int getPort() throws RemoteException;
}
