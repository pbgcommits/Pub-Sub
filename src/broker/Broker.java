package broker;

import java.util.HashMap;
import java.util.Map;

public class Broker {
    private Map<Integer, PublisherAlias> topicPublishers;
    public Broker() {
        topicPublishers = new HashMap<>();
    }
    public void addTopic(int topicId, PublisherAlias publisher) {
        topicPublishers.put(topicId, publisher);
    }
}
