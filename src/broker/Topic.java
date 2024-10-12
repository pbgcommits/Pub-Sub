package broker;

import shared.remote.ITopic;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 * A topic tracks who is subscribed to it. It is published by a specific publisher.
 * @author Patrick Barton Grace 1557198
 */
public class Topic extends UnicastRemoteObject implements ITopic, SubscriberTopic {
    final private Publisher publisher;
    final private List<String> subscribers;
    public List<String> getSubscriberUsernames() {
        return subscribers;
    }
    final private String name;
    public Topic(String name, String id, Publisher publisher) throws RemoteException {
        subscriberCount = 0;
        subscribers = new ArrayList<>();
        this.id = id;
        this.name = name;
        this.publisher = publisher;
        System.out.println("New topic created: " + getString());
    }
    private int subscriberCount;
    final private String id;

    /**
     * Notifies a subscriber that the topic is being deleted from the network.
     * @param username The subscriber to be notified.
     */
    public void removeSelfFromSubscriber(String username) {
//        subscriberCount--;
        publisher.getBroker().deleteTopicFromSubscriber(id, username);
    }

    /**
     * Remove a subscriber from the given topic.
     * @param username
     */
    @Override
    public void removeSubscriber(String username) {
        subscriberCount--;
        subscribers.remove(username);
    }
    @Override
    public void addSubscriber(String username) {
        subscriberCount++;
        subscribers.add(username);
    }
    @Override
    public String getPublisherName() {
        return publisher.getName();
    }
    @Override
    public String getName() {
        return name;
    }
    @Override
    public String getId() {
        return id;
    }
    @Override
    public int getSubscriberCount() {
        return subscriberCount;
    }
    @Override
    public void publishMessage(String message) {
        publisher.getBroker().sendMessageToSubs(id, message, name, getPublisherName(), subscribers);
    }
    @Override
    public String getString() {
        return "Topic id " + id + ": " + name + " (publisher: " + getPublisherName() + ")";
    }

}
