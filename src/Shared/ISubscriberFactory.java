package Shared;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public interface ISubscriberFactory {
    boolean createSubscriber(String username) throws RemoteException;

}
