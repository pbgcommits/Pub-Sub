package Shared;

public interface ITopic {
    void removeSubscriber(String username);
    void addSubscriber(String username);
    String getPublisherName();
    String getName();
    int getId();
    int getSubscriberCount();
    void publishMessage(String message);
}
