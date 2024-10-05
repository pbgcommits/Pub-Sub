package broker;

import Shared.IPublisher;

import javax.naming.LimitExceededException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

public class Publisher extends UnicastRemoteObject implements IPublisher {
//    final private BrokerInfo broker;
    // technically the max amount is actually 1 less than this
    private final static int MAX_TOPIC_COUNT = 1000;
    private int topicCount;
    private int id;
    private static int publisherCount = 0;
    final private Map<Integer, Topic> topics;

    public Map<Integer, Topic> getTopics() {
        return topics;
    }

    final private String name;
    final private Broker broker;
//    final private String ip;
//    final private int port;
    public String getName() {
        return name;
    }
//    public Publisher(String name, String ip, int port) {
    public Publisher(String name, Broker broker) throws RemoteException {
        this.broker = broker;
        topicCount = 0;
        publisherCount++;
        id = publisherCount * MAX_TOPIC_COUNT;
        this.name = name;
//        this.ip = ip;
//        this.port = port;
        topics = new ConcurrentHashMap<>();
    }
    /** Create a New Topic: Generates a unique topic ID (e.g., UUID)
     * and assigns a name (not necessarily unique as multiple publishers may have topics with the same name).
     * */
    @Override
    public int createNewTopic(String name) throws LimitExceededException {
        if (topicCount >= MAX_TOPIC_COUNT) {
            throw new LimitExceededException("Publishers may only host up to " + (MAX_TOPIC_COUNT - 1) + " topics.");
        }
        // TODO : this may cause concurrency issues - if two publishers create a topic at the same time,
        // TODO which topic has which id? what if they both have the same id?!
        // TODO okay I think this should get dealt with by the directory :)
        topicCount++;
        int id = topicCount + this.id;
        Topic t = new Topic(name, id, this);
        topics.put(id, t);
        broker.addTopic(t);
        return id;
    }
//    public int createNewTopic(int id, String name) {
//        // TODO : this may cause concurrency issues - if two publishers create a topic at the same time,
//        // TODO which topic has which id? what if they both have the same id?!
//        topicCount++;
//        topics.put(topicCount, new Topic(name, topicCount, this));
//        return id;
//    }
    /** Publish a Message to an Existing Topic:
     * Sends a message of a topic through its broker by using the unique topic ID.
     * The message should be sent to all topic subscribers. Each message will be limited to a maximum of 100 characters.
     * It is not required to persist messages in any of the brokers.*/
    @Override
    public void publish (int id, String message) throws NoSuchElementException {
        verifyTopic(id);
        topics.get(id).publishMessage(message);
    }
    /** Show Subscriber Count: Shows the total number of subscribers for each topic associated with this publisher.
     * */
    @Override
    public int show (int id) throws NoSuchElementException {
        verifyTopic(id);
//        System.out.println(topics.get(id).getSubscriberCount());
        return topics.get(id).getSubscriberCount();
    }
    /** Delete a Topic: Removes the topic from the system and automatically unsubscribes all current subscribers.
     * A notification message should be sent to each subscriber.
     * */
    @Override
    public void delete (int id) throws NoSuchElementException {
        verifyTopic(id);
        Topic topic = topics.get(id);
        for (String sub : topic.getSubscriberUsernames()) {
            topic.removeSubscriber(sub);
        }
        topics.remove(id);
    }

    public Broker getBroker() {
        return broker;
    }

    /** Checks that the given topicID exists and is registered with this publisher. */
    private void verifyTopic(int id) throws NoSuchElementException {
        if (topics.get(id) == null) {
            throw new NoSuchElementException("Publisher does not have a topic with this id");
        }
    }
}
