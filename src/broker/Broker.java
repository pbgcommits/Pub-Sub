package broker;

import Shared.IBroker;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class Broker extends UnicastRemoteObject implements IBroker {
    private final Map<Integer, Topic> topics;
    private final Map<String, Publisher> publishers;
    private final Map<String, Subscriber> subscribers;
    private final List<IBroker> brokers;
    private int numConnections;
    private int port;
    private String ip;
    private final String id;
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
    Registry registry;
    public Broker(String ip, int port, Registry registry) throws RemoteException {
        this.id = "Broker" + port;
        this.ip = ip;
        this.port = port;
        numConnections = 0;
        topics = new HashMap<>();
        publishers = new HashMap<>();
        subscribers = new HashMap<>();
        brokers = new ArrayList<>();
        this.registry = registry;
//        sf = new SubscriberFactory(registry);
//        pf = new PublisherFactory(registry);
//        try {
//            SubscriberFactory sf = new SubscriberFactory(registry);
////            registry.bind("SubscriberFactory" + id, sf);
//            PublisherFactory pf = new PublisherFactory(registry);
////            registry.bind("PublisherFactory" + id, pf);
//        }
//        catch (AlreadyBoundException e) {
//            System.out.println(e.getMessage());
//        }
    }

    @Override
    public boolean passMessage(Integer topicID, String message, String topicName,
                               String publisherName, String sub) {
        if (subscribers.containsKey(sub)) {
            subscribers.get(sub).writeMessage(message, topicID, topicName, publisherName);
            return true;
        }
        return false;
    }

    public void sendMessageToSubs(Integer topicID, String message, String topicName,
                                  String publisherName, List<String> subs) throws NoSuchElementException {
        if (topics.get(topicID) == null) {
            System.out.println("Sending to non-existent topic");
            throw new NoSuchElementException("This topic does not exist!");
        }
        System.out.println("Sending message " + message + " to " + topicID);
        for (String sub : subs) {
            if (!passMessage(topicID, message, topicName, publisherName, sub)) {
                for (IBroker b : brokers) {
                    if (b.passMessage(topicID, message, topicName, publisherName, sub)) {
                        break;
                    };
                }
            }
        }
    }
    @Override
    public int getNumConnections() {
        return numConnections;
    }
    @Override
    public void addPublisher(String username) throws AlreadyBoundException, RemoteException {
        System.out.println("Adding publisher: " + username);
        Publisher p = new Publisher(username, this);
        registry.bind(username, p);
        publishers.put(username, p);
        numConnections++;
    }
    public void removePublisher(Publisher p) {
        publishers.remove(p);
        numConnections--;
    }
    @Override
    public void addSubscriber(String username) throws AlreadyBoundException, RemoteException {
        System.out.println("Adding subscriber: " + username);
        Subscriber s = new Subscriber(this, username);
        registry.bind(username, s);
        subscribers.put(username, s);
        numConnections++;
    }
    public void removeSubscriber(Subscriber s) {
        subscribers.remove(s);
        numConnections--;
    }
    @Override
    public void addBroker(IBroker b) {
        brokers.add(b);
        numConnections++;
    }
    public void removeBroker(IBroker b) {
        brokers.remove(b);
        numConnections--;
    }
    @Override
    public boolean attemptRemoveTopicForSubscriber(int topicID, String username) {
        if (subscribers.containsKey(username)) {
            subscribers.get(username).deleteTopic(topicID);
            return true;
        }
        return false;
    }
    public void removeTopicForSubscriber(int topicID, String username) {
        boolean success = attemptRemoveTopicForSubscriber(topicID, username);
        if (success) return;
        for (IBroker b : brokers) {
            if (b.attemptRemoveTopicForSubscriber(topicID, username)) {
                return;
            }
        }
        throw new NoSuchElementException("Subscriber could not be found");
    }
    @Override
    public boolean attemptAddSubscriberToTopic(int topicID, String username) {
        for (Publisher p : publishers.values()) {
            if (p.getTopics().containsKey(topicID)) {
                p.getTopics().get(topicID).addSubscriber(username);
                return true;
            }
        }
        return false;
    }
    public void addSubscriberToTopic(int topicID, String username) throws NoSuchElementException {
        if (!attemptAddSubscriberToTopic(topicID, username)) {
            for (IBroker b : brokers) {
                if (b.attemptAddSubscriberToTopic(topicID, username)) return;
            }
        }
        throw new NoSuchElementException("Topic could not be found");
    }
}
