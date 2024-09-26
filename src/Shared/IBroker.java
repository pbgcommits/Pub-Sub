package Shared;

import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IBroker extends Remote {
    String getId();

    int getPort();

    String getIp();

    boolean passMessage(Integer topicID, String message, String topicName,
                        String publisherName, String sub);

    int getNumConnections();

    void addPublisher(String username) throws AlreadyBoundException, RemoteException;

    void addSubscriber(String username) throws AlreadyBoundException, RemoteException;

    void addBroker(IBroker b);

    boolean attemptRemoveTopicForSubscriber(int topicID, String username);

    boolean attemptAddSubscriberToTopic(int topicID, String username);
}
