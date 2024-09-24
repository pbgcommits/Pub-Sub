package Shared;

import java.util.List;

public interface ISubscriber {

    List<String> listAllAvailableTopics();

    void subscribeToTopic(int id);

    void showCurrentSubscriptions();

    void unsubscribe(int id);

    boolean hasMessage();

    String getMessage();
}
