package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.NoSuchElementException;

public interface IDirectory extends Remote {
    void addBroker(String id) throws RemoteException;
    IBroker getMostAvailableBroker() throws NoSuchElementException, RemoteException;

}
