import subscriber.Subscriber;

import java.util.ArrayList;
import java.util.List;

public class Topic {
    private List<Subscriber> subscribers;
    public Topic(int id) {
        subscriberCount = 0;
        subscribers = new ArrayList<>();
        this.id = id;
    }
    private int subscriberCount;
    private int id;
    public int getSubscriberCount() {
        return subscriberCount;
    }
    public void publishMessage(String message) {

    }
}
