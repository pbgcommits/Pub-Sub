package broker.remote;

import java.net.Socket;
import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Remote interface for broker objects. This interface is much more powerful than the interface specified in
 * the shared.remote directory.
 * @author Patrick Barton Grace 1557198
 */
public interface NetworkBroker extends Remote{
//    String getId() throws RemoteException;
//    int getPort() throws RemoteException;
//    String getIp() throws RemoteException;

    /**
     * Attempt to pass a message to the given subscriber from a given topic.
     * @param topicID The topic's id.
     * @param message The message to pass.
     * @param topicName The topic's name.
     * @param publisherName The publisher's name.
     * @param sub The subscriber to which the message should be passed.
     * @return Whether the message was successfully passed.
     * @throws RemoteException If there is an issue with the RMI registry.
     */
    boolean passMessage(String topicID, String message, String topicName,
                        String publisherName, String sub) throws RemoteException;

    /**
     * Get the number of active connections this broker is maintaining.
     */
//    int getNumConnections() throws RemoteException;

    /**
     * Connect a new publisher to the broker.
     * @param client A socket connection to the publisher.
     * @param username The publisher's username.
     * @throws AlreadyBoundException If the publisher already exists in the system.
     * @throws RemoteException If there is an issue with the RMI registry.
     */
    void addPublisher(Socket client, String username) throws AlreadyBoundException, RemoteException;

    /**
     * Connect a new subscriber to the broker.
     * @param client A socket connection to the subscriber.
     * @param username The subscriber's username.
     * @throws AlreadyBoundException If the subscriber already exists in the system.
     * @throws RemoteException If there is an issue with the RMI registry.
     */
    void addSubscriber(Socket client, String username) throws AlreadyBoundException, RemoteException;

    /**
     * Connect a new broker to this broker.
     * @param b The new broker to be connected.
     * @throws RemoteException If there is an issue with the RMI registry.
     */
//    void addBroker(IBroker b) throws RemoteException;

    /**
     * Attempt to delete a given topic from a subscriber.
     * @param topicID The id of the topic.
     * @param username The username of the subscriber.
     * @return Whether the topic was successfully deleted.
     * @throws RemoteException If there is an issue with the RMI registry.
     */
    boolean attemptDeleteTopicFromSubscriber(String topicID, String username) throws RemoteException;

    /**
     * Attempt to remove a given subscriber from a topic.
     * @param topicID The id of the topic.
     * @param username The username of the subscriber.
     * @return Whether the subscribed was successfully removed.
     * @throws RemoteException If there is an issue with the RMI registry.
     */
    boolean attemptDeleteSubscriberFromTopic(String topicID, String username) throws RemoteException;

    /**
     * Attempt to subscribe a given subscriber to a topic.
     * @param topicID The id of the topic.
     * @param username The username of the subscriber.
     * @return A reference to the topic's important information.
     * @throws RemoteException If there is an issue with the RMI registry.
     */
    SubscriberTopic attemptAddSubscriberToTopic(String topicID, String username) throws RemoteException;

    /**
     * Get all topics that are stored in the broker.
     */
    List<ITopic> getTopics() throws RemoteException;
}
