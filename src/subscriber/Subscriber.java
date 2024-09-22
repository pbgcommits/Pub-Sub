package subscriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 2.3 Subscriber
 * • Subscribers are clients that express interest in specific topics by subscribing to them through a broker.
 *   They receive real-time messages on those topics from their broker.
 * • A subscriber should receive a real-time notification message if they unsubscribe from a topic or if a topic they are subscribed to is deleted by the publisher.
 * • There can be up to 10 subscribers present in the system at the same time. */
public class Subscriber {
    private Map<Integer, String> currentTopics; /** Subscribers may subscribe to multiple topics (from the same or different publishers). */
    private String directoryIP; /** A subscriber can connect to only one broker at a time, specified via the command line at runtime. */
    private int directoryPort;
    private String username; /** You may assume that subscriber names will be unique throughout the system.*/
    public Subscriber(String username, String directoryIP, int directoryPort) {
        this.username = username;
        this.directoryIP = directoryIP;
        this.directoryPort = directoryPort;
        currentTopics = new HashMap<>();
    }

    /** List All Available Topics:
     * Retrieves the list of all available topics across the broker network, including topic ID, topic name, and publisher name.
     * */
    public List<String> listAllAvailableTopics() {
        List<String> topics = new ArrayList<>();
        topics.add("Topic 1");
        topics.add("Topic 2");
        topics.add("Topic 3");
        for (String topic : topics) {
            System.out.println(topic);
        }
        return topics;
    }
    /** Subscribe to a Topic:
     * Subscribes to a topic using the topic’s unique ID. The subscriber will receive all future messages published on this topic.
     * */
    public void subscribeToTopic(int id) {
        if (currentTopics.get(id) != null) {
            System.out.println("Already subscribed to this topic!");
            return;
        }
        currentTopics.put(id, "Topic " + id);
        System.out.println("Subscribed to " + currentTopics.get(id));
    }
    /** Show current subscriptions:
     * Lists the active subscriptions with topic ID, topic name, and publisher name.*/
    public void showCurrentSubscriptions() {
        if (currentTopics.isEmpty()) {
            System.out.println("Currently not subscribed to any topics. Subscribe using: sub {topic_id}");
            return;
        }
        System.out.println("Current subscriptions:");
        for (int subID : currentTopics.keySet()) {
            System.out.println(currentTopics.get(subID));
        }
    }
    /** Unsubscribe from a Topic:
     * Stops receiving messages from a topic. The broker sends a notification message confirming the unsubscription.
     * */
    public void unsubscribe(int id) {
        currentTopics.remove(id);
        System.out.println("Unsubscribed from topic " + id + ".");
    }

    /** When a message is published to a subscribed topic,
     * it is immediately displayed on the subscribers console along with the topic ID, topic name, and publisher name.*/
    public void displayMessage(String message, int topicID, String topicName, String publisherName) {
        System.out.println("From " + publisherName + ", about " + topicName + " (" + topicID + "):");
        System.out.println(message);
    }
    /** A subscriber should receive a real-time notification message if they unsubscribe from a topic or if a topic they are subscribed to is deleted by the publisher.
     * (This is specifically to deal with a topic being deleted by its publisher*/
    public void deleteTopic(int id) {
        currentTopics.remove(id);
        System.out.println("Topic " + id + " deleted by its publisher.");
    }
}
