package Shared;

import broker.SubscriberTopic;

import java.net.Socket;
import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.NoSuchElementException;

public interface IBroker extends Remote, Connection {
    String getId() throws RemoteException;

//    int getPort() throws RemoteException;
//
//    String getIp() throws RemoteException;

    boolean passMessage(Integer topicID, String message, String topicName,
                        String publisherName, String sub) throws RemoteException;

    int getNumConnections() throws RemoteException;

    void addPublisher(Socket client, String username) throws AlreadyBoundException, RemoteException;

    void addSubscriber(Socket client, String username) throws AlreadyBoundException, RemoteException;

    void addBroker(IBroker b) throws RemoteException;

    boolean attemptDeleteTopicFromSubscriber(int topicID, String username) throws RemoteException;

    boolean attemptDeleteSubscriberFromTopic(int topicID, String username) throws RemoteException, NoSuchElementException;

    SubscriberTopic attemptAddSubscriberToTopic(int topicID, String username) throws RemoteException;

    List<ITopic> getTopics() throws RemoteException;
}
