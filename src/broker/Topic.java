package broker;

import Shared.ITopic;

import java.util.ArrayList;
import java.util.List;

public class Topic implements ITopic, SubscriberTopic {
    final private Publisher publisher;
    final private List<String> subscribers;
    public List<String> getSubscriberUsernames() {
        return subscribers;
    }
    final private String name;
    public Topic(String name, int id, Publisher publisher) {
        subscriberCount = 0;
        subscribers = new ArrayList<>();
        this.id = id;
        this.name = name;
        this.publisher = publisher;
    }
    private int subscriberCount;
    final private int id;

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
    public int getId() {
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
    public String toString() {
        return "Topic id " + id + ": " + name + " (publisher: " + getPublisherName() + ")";
    }

}
