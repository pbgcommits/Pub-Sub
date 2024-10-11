package shared;

public class Messenger {
    public String getConnectionType(String input) {
        return input.substring(0, 3);
    }
    public String getUsername(String input) {
        return input.substring(4);
    }
    private final static String SUBSCRIBER_PREFIX = "SUB";
    private final static String PUBLISHER_PREFIX = "PUB";
    public final static String DIRECTORY_RMI_NAME = "Directory";
    public String subscriber() {return SUBSCRIBER_PREFIX;}
    public String publisher() {return PUBLISHER_PREFIX;}
    public String newSubscriberMessage(String username) {
        return SUBSCRIBER_PREFIX + ":" + username;
    }
    public String newPublisherMessage(String username) {
        return PUBLISHER_PREFIX + ":" + username;
    }
    public String writeMessageFromTopic(String message, String topicId, String topicName, String publisherName) {
//        return topicName + ":" + topicId + ":" + publisherName + " : " + message;
        return "New message from \"" + topicName + "\" (id " + topicId + ", published by " + publisherName + "): " + message;
    }
    public String writeTopicDeletionMessage(String topicId, String topicName, String publisherName) {
        return "The topic \"" + topicName + "\" (id " + topicId + ") has been deleted by " + publisherName + ".";
    }
    public String writeOnlineMessage() {
        return "ONLINE";
    }
}
