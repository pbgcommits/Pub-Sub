package broker;

import broker.remote.SubscriberTopic;
import broker.remote.NetworkBroker;
import broker.remote.ITopic;
import shared.util.Messenger;
import shared.remote.IBroker;
import broker.connections.PublisherConnection;
import broker.connections.SubscriberConnection;

import java.net.Socket;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The broker deals with connecting publishers and subscribers. It tracks some amount of the total publishers and
 * subscribers connected to the network, as well as all the other brokers in the network.
 * @author Patrick Barton Grace 1557198
 */
public class Broker extends UnicastRemoteObject implements NetworkBroker{
    private final ConcurrentHashMap<String, Topic> topics;
    private final ConcurrentHashMap<String, Publisher> publishers;
    private final ConcurrentHashMap<String, Subscriber> subscribers;
    private final ConcurrentHashMap<Subscriber, SubscriberConnection> subscriberConnections;
    private final ConcurrentHashMap<Publisher, PublisherConnection> publisherConnections;
    private final List<NetworkBroker> brokers;
    // Only tracks publisher and subscriber connections (not brokers)
    private int numConnections;
    private final int port;
    private final String ip;
    private final String id;
    private final Registry registry;
    @Override
    public String getId() {return id;}

    @Override
    public int getPort() {
        return port;
    }
    @Override
    public String getIp() {
        return ip;
    }
    public Broker(String ip, int port, Registry registry) throws RemoteException {
        this.id = "Broker" + port;
        this.ip = ip;
        this.port = port;
        numConnections = 0;
        topics = new ConcurrentHashMap<>();
        publishers = new ConcurrentHashMap<>();
        subscribers = new ConcurrentHashMap<>();
        brokers = new ArrayList<>();
        this.registry = registry;
        subscriberConnections = new ConcurrentHashMap<>();
        publisherConnections = new ConcurrentHashMap<>();
    }

    /**
     * Attempt to send a message to a subscriber. Will return true if successful, or false if this broker
     * didn't have the subscriber's connection information.
     * @param topicID Topic to publish to
     * @param message Message to publish
     * @param topicName
     * @param publisherName
     * @param sub Subscriber to send message to
     * @return Whether this broker had the connection information to the given subscriber.
     */
    @Override
    public boolean passMessage(String topicID, String message, String topicName,
                               String publisherName, String sub) {
        if (subscribers.containsKey(sub)) {
            subscribers.get(sub).sendMessage(new Messenger().writeMessageFromTopic(message, topicID, topicName, publisherName));
            return true;
        }
        return false;
    }

    /**
     * Send a message to every subscriber subscribed to a given topic.
     * @param topicID The topic to send a message to.
     * @param message The message to send.
     * @param topicName The topic's name.
     * @param publisherName The publisher's name.
     * @param subs The list of subscribers to publish the message to.
     * @throws NoSuchElementException If this broker has no records on the given topic.
     */
    public void sendMessageToSubs(String topicID, String message, String topicName,
                                  String publisherName, List<String> subs) throws NoSuchElementException {
        if (topics.get(topicID) == null) {
            System.out.println("Sending to non-existent topic");
            throw new NoSuchElementException("This topic does not exist!");
        }
        System.out.println("Sending message " + message + " to topic " + topicID);
        for (String sub : subs) {
            if (!passMessage(topicID, message, topicName, publisherName, sub)) {
                try {
                    for (NetworkBroker b : brokers) {
                        if (b.passMessage(topicID, message, topicName, publisherName, sub)) {
                            break;
                        }
                        ;
                    }
                }
                catch (RemoteException e) {
                    System.out.println("BROKER IS DISCONNECTED:");
                    e.printStackTrace();
                }
            }
        }
    }
    @Override
    public int getNumConnections() {
        return numConnections;
    }

    /**
     * Connects a new publisher client to this broker.
     * @param client A socket connection to the publisher.
     * @param username The username of the new publisher.
     * @throws RemoteException If the RMI registry has an issue
     */
    @Override
    public void addPublisher(Socket client, String username) throws RemoteException {
//        System.out.println("Adding publisher: " + username);
        Publisher p = new Publisher(username, this);
        try {
            registry.bind(username, p);
            System.out.println("Added new publisher: " + username);
//            System.out.println("Bound " + username + " for the first time");
        }
        catch (AlreadyBoundException e) {
            // Currently, if a publisher already exists with this username, its connection information will be lost
            System.out.println(username + " already exists");
//            registry.rebind(username, p);
        }
        publishers.put(username, p);
        PublisherConnection connection = new PublisherConnection(client, this, p);
        connection.start();
        publisherConnections.put(p, connection);
        numConnections++;
    }

    /**
     * Removes a given publisher and all of its topics from the network.
     * Called only when a publisher disconnects.
     * @param p The publisher to disconnect.
     */
    public void removePublisher(Publisher p) {
        publishers.remove(p);
        for (String id : p.getTopics().keySet()) {
            p.delete(id);
//            topics.remove(id);
        }
//        System.out.println(p.getName() + " has disconnected.");
        numConnections--;
    }

    /**
     * Adds a new subscriber to the network.
     * @param client A socket connection to the subscriber.
     * @param username The subscriber's username.
     * @throws RemoteException If the RMI registry has an error.
     */
    @Override
    public void addSubscriber(Socket client, String username) throws RemoteException {
        Subscriber s = new Subscriber(this, username);
        try {
            registry.bind(username, s);
            System.out.println("Added new subscriber: " + username);
//            System.out.println("Bound " + username + " for the first time");
        }
        catch (AlreadyBoundException e) {
            // Currently, if a publisher already exists with this username, its connection information will be lost:
            // Subsequently, only the most recently added subscriber with this username will be notified of
            // events by the server
            System.out.println(username + " already existed");
//            registry.rebind(username, s);
        }
        subscribers.put(username, s);
        SubscriberConnection connection = new SubscriberConnection(client, this, s);
        connection.start();
        subscriberConnections.put(s, connection);
        numConnections++;
    }

    @Override
    public void attemptRemoveSubscriber(String username, Set<String> userTopics) {
        for (String id : userTopics) {
            if (this.topics.get(id) != null) {
                this.topics.get(id).removeSubscriber(username);
            }
        }
    }

    /**
     * Removes a subscriber from the network.
     * Should only be called when a subscriber disconnects.
     * @param s The subscriber to be removed.
     */
    public void removeSubscriber(Subscriber s) {
        String username = s.getName();
        Set<String> topics = s.getTopics();
        attemptRemoveSubscriber(username, topics);
        for (NetworkBroker b : brokers) {
            try {
                b.attemptRemoveSubscriber(username, topics);
            }
            catch (RemoteException e) {
                System.out.println("Failed to connect to a broker while deleting subscriber");
            }
        }
        subscribers.remove(s);
        System.out.println(s.getName() + " has disconnected.");
        numConnections--;
    }

    /**
     * Add a new broker to the network.
     * @param b The broker to add.
     */
    @Override
    public void addBroker(IBroker b) {
        NetworkBroker nb = (NetworkBroker) b;
        if (brokers.contains(nb) || b.equals(this)) return; // do I need this?
        brokers.add(nb);
//        System.out.println("added a new broker");
//        numConnections++;
    }
    public void removeBroker(NetworkBroker b) {
        brokers.remove(b);
        System.out.println("A broker has disconnected.");
        numConnections--;
    }

    /**
     * Attempts to remove a given topic from a certain subscriber.
     * @param topicID The topic to be removed's id.
     * @param username The subscriber's username.
     * @return True if this broker managed this subscriber, and successfully deleted the topic from it;
     *         false otherwise.
     * @throws NoSuchElementException If the subscriber wasn't already subscribed to the topic.
     */
    @Override
    public boolean attemptDeleteTopicFromSubscriber(String topicID, String username) throws NoSuchElementException {
//        System.out.println("DELETING TOPIC FROM SUBSCRIBER");
        if (subscribers.containsKey(username)) {
            System.out.println("Deleting topic from subscriber " + username);
            subscribers.get(username).deleteTopic(topicID);
            return true;
        }
        return false;
    }

    /**
     * Given a topic and subscriber, communicate to the subscriber that this topic is being deleted.
     * @param topicID The topic to be deleted.
     * @param username The username of the subscriber who needs the topic deleted.
     * @throws NoSuchElementException Either the subscriber was not subscribed to the topic, or the subscriber is
     *                                not present in the network.
     */
    public void deleteTopicFromSubscriber(String topicID, String username) throws NoSuchElementException {
//        System.out.println("TOPIC IS GETTING DELETED D:");
        boolean success = attemptDeleteTopicFromSubscriber(topicID, username);
        if (success) {
            return;
        }
        try {
            for (NetworkBroker b : brokers) {
                if (b.attemptDeleteTopicFromSubscriber(topicID, username)) {
                    return;
                }
            }
        }
        catch (RemoteException e) {
            System.out.println("A BROKER DISCONNECTED");
            e.printStackTrace();
        }
        throw new NoSuchElementException("Subscriber could not be found");
    }

    /**
     * Remove a subscriber from a topic it was previously subscribed to.
     * @param topicID
     * @param username
     * @return True if the subscriber successfully unsubscribed; false otherwise.
     * @throws NoSuchElementException If the subscriber wasn't already subscribed to this topic.
     */
    @Override
    public boolean attemptDeleteSubscriberFromTopic(String topicID, String username) {
        if (topics.containsKey(topicID)) {
            topics.get(topicID).removeSubscriber(username);
            return true;
        }
        return false;
    }

    /**
     * Given a topic and subscriber, communicate to the topic that the subscriber is unsubscribing from it.
     * @param topicID The topic to be deleted.
     * @param username The username of the subscriber who needs the topic deleted.
     * @throws NoSuchElementException Either the subscriber was not subscribed to the topic, or the subscriber is
     *                                not present in the network.
     */
    public void deleteSubscriberFromTopic(String topicID, String username) throws NoSuchElementException {
        boolean success = attemptDeleteSubscriberFromTopic(topicID, username);
        if (success) {
            return;
        }
        try {
            for (NetworkBroker b : brokers) {
                if (b.attemptDeleteSubscriberFromTopic(topicID, username)) {
                    return;
                }
            }
        }
        catch (RemoteException e) {
            System.out.println("A BROKER DISCONNECTED");
//            e.printStackTrace();
        }
        throw new NoSuchElementException("Subscriber could not be found");
    }

    @Override
    public SubscriberTopic attemptAddSubscriberToTopic(String topicID, String username) {
        if (!topics.containsKey(topicID)) {
            return null;
        }
        ITopic t = topics.get(topicID);
        try {
            t.addSubscriber(username);
        }
        catch (RemoteException e) {
            System.out.println("Failed to subscribe " + username + " to topic");
            return null;
        }
        return topics.get(topicID);
//        for (Publisher p : publishers.values()) {
//            if (p.getTopics().containsKey(topicID)) {
//                ITopic t = p.getTopics().get(topicID);
//                try {
//                    t.addSubscriber(username);
//                }
//                catch (RemoteException e) {
//                    System.out.println("Failed to subscribe " + username + " to topic");
//                    return null;
//                }
//                SubscriberTopic st = p.getTopics().get(topicID);
//                return st;
//            }
//        }
//        return null;
    }

    /**
     * Subscribe a given subscriber to a new topic.
     * @param topicID The topic's id.
     * @param username The subscriber's username.
     * @return A reference to the topic the subscriber has subscribed to.
     * @throws NoSuchElementException If there is no topic with that id.
     */
    public SubscriberTopic addSubscriberToTopic(String topicID, String username) throws NoSuchElementException {
        SubscriberTopic t = attemptAddSubscriberToTopic(topicID, username);
        if (t != null) {
            return t;
        }
        else  {
            try {
                for (NetworkBroker b : brokers) {
                    t = b.attemptAddSubscriberToTopic(topicID, username);
                    if (t != null) return t;
                }
            }
            catch (RemoteException e) {
                System.out.println("COULDN'T CONNECT TO BROKER:");
                e.printStackTrace();
            }
        }
        throw new NoSuchElementException("Topic could not be found");
    }

    @Override
    public List<ITopic> getTopics() {
        return new ArrayList<>(topics.values());
    }

    /**
     * Create a list of all available topics on the network.
     * @return The list of all available topics, returned as a formatted string.
     */
    public String getAllTopics() {
        StringBuilder sb = new StringBuilder();
        for (Topic t : topics.values()) {
            sb.append(t.getString() + "\n");
        }
        for (NetworkBroker b : brokers) {
            try {
                for (ITopic t : b.getTopics()) {
                    sb.append(t.getString() + "\n");
                }
            }
            catch (RemoteException e) {
                System.out.println("Getting topics failed");
                sb.append("Partial list; could not connect to some brokers");
                e.printStackTrace();
            }
        }
        // remove final newline
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }
    public void addTopic(Topic t) {
        topics.put(t.getId(), t);
    }
    public void removeTopic(String topicId) {
        topics.remove(topicId);
    }
}
