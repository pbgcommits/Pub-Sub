package Shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ISubscriber extends Remote {

    String listAllAvailableTopics() throws RemoteException;

    void subscribeToTopic(int id) throws RemoteException;

    String showCurrentSubscriptions() throws RemoteException;

    void unsubscribe(int id) throws RemoteException;

    boolean hasMessage() throws RemoteException;

    String getMessage() throws RemoteException;
}
