package broker;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SubscriberTopic extends Remote {
    String getString() throws RemoteException;
    String getPublisherName() throws RemoteException;
    String getId() throws RemoteException;
    String getName() throws RemoteException;
}
