package publisher;

import Shared.RemoteTopic;
import subscriber.Subscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Topic implements RemoteTopic {
    final private Publisher publisher;
    final private Map<String, Subscriber> subscribers;
    public Set<String> getSubscriberUsernames() {
        return subscribers.keySet();
    }
    final private String name;
    // TODO: desperately need to work out how I want to store topic ids
    public Topic(String name, int id, Publisher publisher) {
        subscriberCount = 0;
        subscribers = new ConcurrentHashMap<>();
        this.id = id;
        this.name = name;
        this.publisher = publisher;
    }
    private int subscriberCount;
    final private int id;
    @Override
    public synchronized void removeSubscriber(String username) {
        subscriberCount--;
        subscribers.get(username).deleteTopic(id);
        subscribers.remove(username);
    }
    @Override
    public synchronized void addSubscriber(String username) {
        subscriberCount++;
        // TODO: will need to go to the RMI registry and find the subscriber
//        subscribers.put(username, )
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
        String pubName = getPublisherName();
        for (String s : subscribers.keySet()) {
            subscribers.get(s).displayMessage(message, id, name, pubName);
        }
    }
}
