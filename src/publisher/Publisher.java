package publisher;

import broker.Broker;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

public class Publisher {
//    final private BrokerInfo broker;
    private static int topicCount = 0;
//    final private Map<Integer, Topic> topics;
    public Publisher() {

//        topics = new ConcurrentHashMap<>();
    }
    public void create(int id, String name) {
        // TODO : this may cause concurrency issues - if two publishers create a topic at the same time,
        // TODO which topic has which id? what if they both have the same id?!
        topicCount++;
//        topics.put(topicCount, new Topic(topicCount));
    }
    public void publish (int id, String message) {
        if (topics.get(id) == null) {
            throw new NoSuchElementException("Publisher does not have a topic with this id");
        }
        topics.get(id).publishMessage(message);
    }
    public int show (int id) {
        return topics.get(id).getSubscriberCount();
    }
    public void delete (int id) {

    }
}
