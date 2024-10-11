package broker;

import shared.ISubscriber;
import shared.Messenger;
import shared.SubscriberCommand;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/**
 * 2.3 Subscriber
 * • Subscribers are clients that express interest in specific topics by subscribing to them through a broker.
 *   They receive real-time messages on those topics from their broker.
 * • A subscriber should receive a real-time notification message if they unsubscribe from a topic or if a topic they are subscribed to is deleted by the publisher.
 * • There can be up to 10 subscribers present in the system at the same time. */
public class Subscriber extends UnicastRemoteObject implements ISubscriber {
    private String message = null;
    private Map<String, SubscriberTopic> currentTopics; /** Subscribers may subscribe to multiple topics (from the same or different publishers). */
    private String directoryIP; /** A subscriber can connect to only one broker at a time, specified via the command line at runtime. */
    private int directoryPort;
    private final Broker broker;
    private String username; /** You may assume that subscriber names will be unique throughout the system.*/
    public Subscriber(Broker broker, String username) throws RemoteException {
        this.username = username;
        this.broker = broker;
        currentTopics = new HashMap<>();
    }

    /** List All Available Topics:
     * Retrieves the list of all available topics across the broker network, including topic ID, topic name, and publisher name.
     * */
    @Override
    public String listAllAvailableTopics() {
        System.out.println(getName() + "is requesting list of all available topics.");
        return broker.getAllTopics();
    }
    /** Subscribe to a Topic:
     * Subscribes to a topic using the topic’s unique ID. The subscriber will receive all future messages published on this topic.
     * */
    @Override
    public void subscribeToTopic(String id) throws NoSuchElementException, IllegalArgumentException {
        System.out.println(getName() + " is attempting to subscribe to topic " + id + ".");
        if (currentTopics.get(id) != null) {
            throw new IllegalArgumentException("Already subscribed to this topic!");
        }
        try {
            SubscriberTopic t = broker.addSubscriberToTopic(id, username);
//        TODO need to also store topic name and publisher :)))
            currentTopics.put(id, t);
        }
        catch (NoSuchElementException e) {
            System.out.println("Topic " + id + " does not exist");
            throw new NoSuchElementException("Topic " + id + " does not exist");
        }
        try {
            System.out.println(getName() + " subscribed to " + currentTopics.get(id).getString());
        }
        catch (RemoteException e) {
            System.out.println("RMI error related to topic subscription. ");
        }
    }
    /** Show current subscriptions:
     * Lists the active subscriptions with topic ID, topic name, and publisher name.*/
    @Override
    public String showCurrentSubscriptions() {
        System.out.println(getName() + "is requesting their current subscriptions.");
        if (currentTopics.isEmpty()) {
            return "Currently not subscribed to any topics. Subscribe using: " + SubscriberCommand.SUB.getUsage();
        }
        StringBuilder sb = new StringBuilder("Current subscriptions:\n");
        for (SubscriberTopic t : currentTopics.values()) {
            try {
                sb.append(t.getString() + "\n");
            }
            catch (RemoteException e) {
                return "Error collecting topic names.";
            }
        }
        return sb.toString();
    }
    /** Unsubscribe from a Topic:
     * Stops receiving messages from a topic. The broker sends a notification message confirming the unsubscription.
     * */
    @Override
    public void unsubscribe(String id) throws IllegalArgumentException {
        if (currentTopics.get(id) == null) {
            throw new IllegalArgumentException("You are not subscribed to this topic!");
        }
        broker.deleteSubscriberFromTopic(id, username);
        currentTopics.remove(id);
        System.out.println(getName() +  " unsubscribed from topic " + id + ".");
    }

    /** When a message is published to a subscribed topic,
     * it is immediately displayed on the subscribers console along with the topic ID, topic name, and publisher name.*/
    public void sendMessage(String message) {
        this.message = message;
    }
    @Override
    public boolean hasMessage() {
        return message != null;
    }

    /**
     * Gets the current message waiting to be sent to subscriber, then removes it from the queue.
     * @return
     */
    @Override
    public String getMessage() {
        String message = this.message;
        this.message = null;
        return message;
    }
    /** A subscriber should receive a real-time notification message if they unsubscribe from a topic
     * or if a topic they are subscribed to is deleted by the publisher.
     * (This is specifically to deal with a topic being deleted by its publisher) */
    public void deleteTopic(String id) throws NoSuchElementException {
        SubscriberTopic t = currentTopics.get(id);
        if (t == null) {
            // I'm not sure this should ever happen?
            throw new NoSuchElementException("You are not subscribed to this topic!");
        }
        try {
            sendMessage(new Messenger().writeTopicDeletionMessage(id, t.getName(), t.getPublisherName()));
        }
        catch (RemoteException e) {
            System.out.println("Remote issue");
        }
        currentTopics.remove(id);
        System.out.println("Topic " + id + " deleted by its publisher.");
    }
    public Set<String> getTopics() {
        return currentTopics.keySet();
    }
    public String getName() {
        return username;
    }
}