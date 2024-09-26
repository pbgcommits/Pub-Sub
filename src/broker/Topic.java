package broker;

import Shared.ITopic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Topic implements ITopic {
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
    @Override
    public synchronized void removeSubscriber(String username) {
        subscriberCount--;
        publisher.getBroker().removeTopicForSubscriber(id, username);
//        subscribers.get(username).deleteTopic(id);
        subscribers.remove(username);
    }
    @Override
    public synchronized void addSubscriber(String username) {
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
}
