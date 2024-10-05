package broker;

import Shared.ISubscriber;
import Shared.Messenger;
import subscriber.SubscriberCommand;

import java.net.Socket;
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
    private Map<Integer, SubscriberTopic> currentTopics; /** Subscribers may subscribe to multiple topics (from the same or different publishers). */
    private String directoryIP; /** A subscriber can connect to only one broker at a time, specified via the command line at runtime. */
    private int directoryPort;
    private final Broker broker;
//    private final SubscriberConnection connection;
    private String username; /** You may assume that subscriber names will be unique throughout the system.*/
    //    public Subscriber(String username, String directoryIP, int directoryPort) {
    public Subscriber(Broker broker, String username) throws RemoteException {
        this.username = username;
        this.broker = broker;
        //        this.directoryIP = directoryIP;
        //        this.directoryPort = directoryPort;
        currentTopics = new HashMap<>();

//        connection = new SubscriberConnection(client, broker, this);
//        connection.start();
    }

    /** List All Available Topics:
     * Retrieves the list of all available topics across the broker network, including topic ID, topic name, and publisher name.
     * */
    @Override
    public String listAllAvailableTopics() {
        System.out.println("Requesting list of all available topics");
//        if (broker.getAllTopics().equals("")) return "No topics currently available.";
        return broker.getAllTopics();
//        List<String> topics = new ArrayList<>();
//        topics.add("Topic 1");
//        topics.add("Topic 2");
//        topics.add("Topic 3");
//        StringBuilder sb = new StringBuilder("All currently available topics:\n");
//        for (String topic : topics) {
////            System.out.println(topic);
//            sb.append(topic + "\n");
//        }
//        return sb.toString();
    }
    /** Subscribe to a Topic:
     * Subscribes to a topic using the topic’s unique ID. The subscriber will receive all future messages published on this topic.
     * */
    @Override
    public void subscribeToTopic(int id) throws NoSuchElementException {
//        if (currentTopics.get(id) != null) {
////            System.out.println("Already subscribed to this topic!");
//            throw new IllegalArgumentException("Already subscribed to this topic!");
////            return "Already subscribed to this topic!";
//        }
        try {
            SubscriberTopic t = broker.addSubscriberToTopic(id, username);
//        TODO need to also store topic name and publisher :)))
            currentTopics.put(id, t);
        }
        catch (NoSuchElementException e) {
            System.out.println("Topic " + id + " does not exist");
            throw new NoSuchElementException("Topic " + id + " does not exist");
        }
//        System.out.println("Subscribed to " + currentTopics.get(id));
//        return "Subscribed to " + currentTopics.get(id);
    }
    /** Show current subscriptions:
     * Lists the active subscriptions with topic ID, topic name, and publisher name.*/
    @Override
    public String showCurrentSubscriptions() {
        if (currentTopics.isEmpty()) {
//            System.out.println("Currently not subscribed to any topics. Subscribe using: sub {topic_id}");
            return "Currently not subscribed to any topics. Subscribe using: " + SubscriberCommand.SUB.getUsage();
        }
        StringBuilder sb = new StringBuilder("Current subscriptions:\n");
        for (SubscriberTopic t : currentTopics.values()) {
            sb.append(t.toString() + "\n");
        }
//        System.out.println("Current subscriptions:");
//        for (int subID : currentTopics.keySet()) {
//            sb.append(currentTopics.get(subID) + "\n");
//            System.out.println(currentTopics.get(subID));
//        }
        return sb.toString();
    }
    /** Unsubscribe from a Topic:
     * Stops receiving messages from a topic. The broker sends a notification message confirming the unsubscription.
     * */
    @Override
    public void unsubscribe(int id) throws IllegalArgumentException {
        if (currentTopics.get(id) == null) {
            throw new IllegalArgumentException("You are not subscribed to this topic!");
//            return "You are not subscribed to this topic!";
        }
        currentTopics.remove(id);
        broker.removeTopicForSubscriber(id, username);
//        System.out.println("Unsubscribed from topic " + id + ".");
//        return "Unsubscribed from topic " + id + ".";
    }

    /** When a message is published to a subscribed topic,
     * it is immediately displayed on the subscribers console along with the topic ID, topic name, and publisher name.*/
    // TODO this will also need to handle a disconnect request (but maybe will be handled somewhere else)
    public void sendMessage(String message) {
        this.message = message;
//        this.message = "From " + publisherName + ", about " + topicName + " (" + topicID + "):" + message;
        //        System.out.println("From " + publisherName + ", about " + topicName + " (" + topicID + "):");
        //        System.out.println(message);
    }
    @Override
    public boolean hasMessage() {
        return message != null;
    }
    @Override
    public String getMessage() {
        String message = this.message;
        this.message = null;
        return message;
    }
    /** A subscriber should receive a real-time notification message if they unsubscribe from a topic
     * or if a topic they are subscribed to is deleted by the publisher.
     * (This is specifically to deal with a topic being deleted by its publisher) */
    public void deleteTopic(int id) {
        // TODO: will have to get the topic name and publisher properly
        SubscriberTopic t = currentTopics.get(id);
        if (t == null) {
            // I'm not sure this should ever happen?
            throw new NoSuchElementException("");
        }
        sendMessage(new Messenger().writeTopicDeletionMessage(id, t.getName(), t.getPublisherName()));
//                "Topic " + id + " deleted by its publisher.", id,
//                t.getName(), t.getPublisherName());
        currentTopics.remove(id);
        //        System.out.println("Topic " + id + " deleted by its publisher.");
    }
    public Set<Integer> getTopics() {
        return currentTopics.keySet();
    }
    public String getName() {
        return username;
    }
}