package Shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

@Deprecated
public interface ISubscriberFactory extends Remote {
    boolean createSubscriber(String username) throws RemoteException;

}
