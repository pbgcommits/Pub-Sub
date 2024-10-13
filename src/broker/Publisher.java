package broker;

import shared.remote.IPublisher;

import javax.naming.LimitExceededException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Server-side class for publishers. Publishers can create topics which subscribers can subscribe to.
 * They can then publish messages to their topics which the subscribers will get in real time.
 * @author Patrick Barton Grace 1557198
 */
public class Publisher extends UnicastRemoteObject implements IPublisher {
    final private ConcurrentHashMap<String, Topic> topics;
    final private String name;
    final private Broker broker;
    public Map<String, Topic> getTopics() {
        return topics;
    }
    public String getName() {
        return name;
    }
    public Publisher(String name, Broker broker) throws RemoteException {
        this.broker = broker;
        this.name = name;
        topics = new ConcurrentHashMap<>();
    }
    /** Create a New Topic: Generates a unique topic ID (e.g., UUID)
     * and assigns a name (not necessarily unique as multiple publishers may have topics with the same name).
     * */
    @Override
    public void createNewTopic(String name, String id) throws RemoteException {
        Topic t = new Topic(name, id, this);
        topics.put(id, t);
        broker.addTopic(t);
    }
    /** Publish a Message to an Existing Topic:
     * Sends a message of a topic through its broker by using the unique topic ID.
     * The message should be sent to all topic subscribers. Each message will be limited to a maximum of 100 characters.
     * It is not required to persist messages in any of the brokers.*/
    @Override
    public void publish (String id, String message) throws NoSuchElementException {
        verifyTopic(id);
        topics.get(id).publishMessage(message);
    }
    /** Show Subscriber Count: Shows the total number of subscribers for each topic associated with this publisher.
     * */
    @Override
    public int show (String id) throws NoSuchElementException {
        verifyTopic(id);
//        System.out.println(topics.get(id).getSubscriberCount());
        return topics.get(id).getSubscriberCount();
    }
    /** Delete a Topic: Removes the topic from the system and automatically unsubscribes all current subscribers.
     * A notification message should be sent to each subscriber.
     * */
    @Override
    public void delete (String id) throws NoSuchElementException {
        verifyTopic(id);
        broker.removeTopic(id);
        Topic topic = topics.get(id);
        ListIterator<String> iter = topic.getSubscriberUsernames().listIterator();
        while (iter.hasNext()) {
            try {
                topic.removeSelfFromSubscriber(iter.next());
                iter.remove();
            }
            catch (NoSuchElementException e) {
                // If we try to remove a subscriber from a topic it was already unsubscribed from
                System.out.println("Invalid sub-topic removal");
            }
        }
        topics.remove(id);
        System.out.println("Deleted topic " + id);
    }

    public Broker getBroker() {
        return broker;
    }

    /** Checks that the given topicID exists and is registered with this publisher. */
    private void verifyTopic(String id) throws NoSuchElementException {
        if (topics.get(id) == null) {
            throw new NoSuchElementException("Publisher does not have a topic with this id");
        }
    }
}
