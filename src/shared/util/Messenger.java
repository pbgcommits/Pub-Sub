package shared.util;

/**
 * Handles creating messages sent over sockets.
 * @author Patrick Barton Grace 1557198
 */
public class Messenger {
    private final static String SUBSCRIBER_PREFIX = "SUB";
    private final static String PUBLISHER_PREFIX = "PUB";
    public final static String DIRECTORY_RMI_NAME = "Directory";
    /** Check the type of incoming connection request (subscriber, publisher, or invalid).*/
    private String getConnectionType(String input) {
        return input.substring(0, 3);
    }

    /**
     * Check the incoming connection request's preferred username.
     * @param input
     * @return
     */
    public String getUsername(String input) {
        return input.substring(4);
    }

    /**
     * Check if the input is requesting a subscriber connection.
     * @param input
     * @return Whether the input is requesting a subscriber connection.
     */
    public boolean isSubscriber(String input) {
        return getConnectionType(input).equals(SUBSCRIBER_PREFIX);
    }

    /**
     * Check if the input is requesting a publisher connection.
     * @param input
     * @return Whether the input is requesting a publisher connection.
     */
    public boolean isPublisher(String input) {
        return getConnectionType(input).equals(PUBLISHER_PREFIX);
    }

    /**
     * Get a correctly formatted message that can be sent to the server to request a new subscriber connection.
     * @param username The desired username.
     * @return The correctly formatted message.
     */
    public String newSubscriberMessage(String username) {
        return SUBSCRIBER_PREFIX + ":" + username;
    }
    /**
     * Get a correctly formatted message that can be sent to the server to request a new publisher connection.
     * @param username The desired username.
     * @return The correctly formatted message.
     */
    public String newPublisherMessage(String username) {
        return PUBLISHER_PREFIX + ":" + username;
    }

    /**
     * Create a new message that is being sent from a given topic.
     * @param message The desired message to be sent.
     * @param topicId
     * @param topicName
     * @param publisherName
     * @return The correctly formatted message.
     */
    public String writeMessageFromTopic(String message, String topicId, String topicName, String publisherName) {
        return "New message from \"" + topicName + "\" (id: " + topicId + ", publisher: " + publisherName + "): " + message;
    }

    /**
     * Create a new message to notify a subscriber that a topic they are subscribed to has been deleted.
     * @param topicId
     * @param topicName
     * @param publisherName
     * @return The correctly formatted message.
     */
    public String writeTopicDeletionMessage(String topicId, String topicName, String publisherName) {
        return "The topic \"" + topicName + "\" (id " + topicId + ") has been deleted by " + publisherName + ".";
    }

    /**
     * Create a message to confirm to a server or client that the sender is still active.
     * @return The correctly formatted message.
     */
    public String writeOnlineMessage() {
        return "ONLINE";
    }
}
