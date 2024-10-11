package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.NoSuchElementException;

public interface ISubscriber extends Remote {

    String listAllAvailableTopics() throws RemoteException;

    void subscribeToTopic(String id) throws RemoteException, NoSuchElementException, IllegalArgumentException;

    String showCurrentSubscriptions() throws RemoteException;

    void unsubscribe(String id) throws RemoteException, IllegalArgumentException;

    boolean hasMessage() throws RemoteException;

    String getMessage() throws RemoteException;

}
