package Shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.NoSuchElementException;

public interface ISubscriber extends Remote {

    String listAllAvailableTopics() throws RemoteException;

    void subscribeToTopic(int id) throws RemoteException, NoSuchElementException;

    String showCurrentSubscriptions() throws RemoteException;

    void unsubscribe(int id) throws RemoteException, IllegalArgumentException;

    boolean hasMessage() throws RemoteException;

    String getMessage() throws RemoteException;
}
