package shared.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote interface for broker objects.
 * @author Patrick Barton Grace 1557198
 */
public interface IBroker extends Remote{
    String getId() throws RemoteException;
    int getPort() throws RemoteException;
    String getIp() throws RemoteException;
    void addBroker(IBroker b) throws RemoteException;

    /**
     * Get the number of active connections this broker is maintaining.
     */
    int getNumConnections() throws RemoteException;
}
