package shared.remote;

import javax.naming.LimitExceededException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.NoSuchElementException;

/**
 * Remote interface for publisher objects.
 * @author Patrick Barton Grace 1557198
 */
public interface IPublisher extends Remote {

    /**
     * Creates a new topic with the given name and id.
     * @param name The name of the new topic.
     * @param id The id of the new topic.
     */
    void createNewTopic(String name, String id) throws RemoteException;

    /**
     * Publish a message to a topic.
     * @param id The topic's id.
     * @param message The message to publish.
     * @throws NoSuchElementException If the topic does not exist.
     */
    void publish(String id, String message) throws NoSuchElementException, RemoteException;

    /**
     * Show how many subscribers are currently subscribed to a given topic.
     * @param id The id of the topic.
     * @return The number of subscribers the topic has.
     * @throws NoSuchElementException If the topic does not exist.
     */
    int show(String id) throws NoSuchElementException, RemoteException;

    /**
     * Delete a given topic from the network.
     * @param id The id of the topic.
     * @throws NoSuchElementException If the topic does not exist.
     */
    void delete(String id) throws NoSuchElementException, RemoteException;
}
