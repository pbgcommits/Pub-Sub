package broker;

import Shared.IBroker;
import Shared.ISubscriber;
import Shared.ITopic;
import Shared.Messenger;

import java.net.Socket;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class Broker extends UnicastRemoteObject implements IBroker {
    private final Map<Integer, Topic> topics;
    private final Map<String, Publisher> publishers;
    private final Map<String, Subscriber> subscribers;
    private final Map<Subscriber, SubscriberConnection> subscriberConnections;
    private final Map<Publisher, PublisherConnection> publisherConnections;
    private final List<IBroker> brokers;
    private int numConnections;
    private final int port;
    private final String ip;
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
        subscriberConnections = new HashMap<>();
        publisherConnections = new HashMap<>();
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
            subscribers.get(sub).sendMessage(new Messenger().writeMessageFromTopic(message, topicID, topicName, publisherName));
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
                try {
                    for (IBroker b : brokers) {
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
    @Override
    public void addPublisher(Socket client, String username) throws AlreadyBoundException, RemoteException {
//        System.out.println("Adding publisher: " + username);
        Publisher p = new Publisher(username, this);
        // this currently uses REBIND - meaning if a subscriber with the same name previously existed,
        // it's now been deleted
        try {
            registry.bind(username, p);
            System.out.println("Bound " + username + " for the first time");
        }
        catch (AlreadyBoundException e) {
            System.out.println(username + " already exists: rebinding");
            registry.rebind(username, p);
        }
        publishers.put(username, p);
        PublisherConnection connection = new PublisherConnection(client, this, p);
        connection.start();
        publisherConnections.put(p, connection);
        numConnections++;
    }
    public void removePublisher(Publisher p) {
        publishers.remove(p);
        for (int id : p.getTopics().keySet()) {
            topics.remove(id);
        }
        numConnections--;
    }
    @Override
    public void addSubscriber(Socket client, String username) throws RemoteException {
        Subscriber s = new Subscriber(this, username);
        // this currently uses REBIND - meaning if a subscriber with the same name previously existed,
        // it's now been deleted
        try {
            registry.bind(username, s);
            System.out.println("Bound " + username + " for the first time");
        }
        catch (AlreadyBoundException e) {
            System.out.println(username + " already existed: rebinding");
            registry.rebind(username, s);
        }
        subscribers.put(username, s);
        SubscriberConnection connection = new SubscriberConnection(client, this, s);
        connection.start();
        subscriberConnections.put(s, connection);
        numConnections++;
    }
    public void removeSubscriber(Subscriber s) {
        for (int id : s.getTopics()) {
            topics.get(id).removeSubscriber(s.getName());
        }
        subscribers.remove(s);
        System.out.println(s.getName() + " has disconnected");
        numConnections--;
    }
    @Override
    public void addBroker(IBroker b) {
        if (brokers.contains(b)) return; // do I need this?
        brokers.add(b);
        System.out.println("added a new broker");
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
        try {
            for (IBroker b : brokers) {
                if (b.attemptRemoveTopicForSubscriber(topicID, username)) {
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
    @Override
    public SubscriberTopic attemptAddSubscriberToTopic(int topicID, String username) {
        for (Publisher p : publishers.values()) {
            if (p.getTopics().containsKey(topicID)) {
                ITopic t = p.getTopics().get(topicID);
                t.addSubscriber(username);
                SubscriberTopic st = p.getTopics().get(topicID);
                return st;
            }
        }
        return null;
    }
    public SubscriberTopic addSubscriberToTopic(int topicID, String username) throws NoSuchElementException {
        SubscriberTopic t = attemptAddSubscriberToTopic(topicID, username);
        if (t != null) {
            return t;
        }
        else  {
            try {
                for (IBroker b : brokers) {
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

    public String getAllTopics() {
        StringBuilder sb = new StringBuilder();
        for (ITopic t : getTopics()) {
            sb.append(t.toString() + "\n");
        }
        for (IBroker b : brokers) {
            try {
                for (ITopic t : b.getTopics()) {
                    sb.append(t.toString() + "\n");
                }
            }
            catch (RemoteException e) {
                System.out.println("COULDN'T CONNECT TO BROKER:");
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    public void addTopic(Topic t) {
        topics.put(t.getId(), t);
    }
    public void removeTopic(Topic t) {
        topics.remove(t);
    }
}
