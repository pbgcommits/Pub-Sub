package broker.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote interface for topic objects.
 * @author Patrick Barton Grace 1557198
 */
public interface ITopic extends Remote {
    void removeSubscriber(String username) throws RemoteException;
    void addSubscriber(String username) throws RemoteException;
    String getPublisherName() throws RemoteException;
    String getName() throws RemoteException;
    String getId() throws RemoteException;
    int getSubscriberCount() throws RemoteException;
    void publishMessage(String message) throws RemoteException;
    String getString() throws RemoteException;
}
