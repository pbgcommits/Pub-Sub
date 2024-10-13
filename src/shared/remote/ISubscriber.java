package shared.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.NoSuchElementException;

/**
 * Remote interface for subscriber objects.
 * @author Patrick Barton Grace 1557198
 */
public interface ISubscriber extends Remote {

    /**
     * Find what available topics are on the network to subscribe to.
     */
    String listAllAvailableTopics() throws RemoteException;

    /**
     * Subscribe to a given topic.
     * @param id The topic's id.
     * @throws NoSuchElementException If there is no topic in the network with the given id.
     * @throws IllegalArgumentException If the subscriber is already subscribed to this topic.
     */
    void subscribeToTopic(String id) throws RemoteException, NoSuchElementException, IllegalArgumentException;

    /**
     * Show all topics which the subscriber is currently subscribed to.
     */
    String showCurrentSubscriptions() throws RemoteException;

    /**
     * Unsubscribe from a given topic.
     * @param id The topic's id.
     * @throws IllegalArgumentException If the subscriber is not subscribed to this topic
     * (or if the topic does not exist).
     */
    void unsubscribe(String id) throws RemoteException, IllegalArgumentException, NoSuchElementException;

    /**
     * True if the subscriber has been sent a message from one of its topics.
     */
    boolean hasMessage() throws RemoteException;

    /**
     * Gets the most recent message sent to the subscriber.
     */
    String getMessage() throws RemoteException;

}
