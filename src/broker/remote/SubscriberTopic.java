package broker.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * An implementation of Topic which is accessed by Subscribers.
 * @author Patrick Barton Grace 1557198
 */
public interface SubscriberTopic extends Remote {
    String getString() throws RemoteException;
    String getPublisherName() throws RemoteException;
    String getId() throws RemoteException;
    String getName() throws RemoteException;
}
