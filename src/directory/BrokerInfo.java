package directory;

public class BrokerInfo {
    public static final int MAX_PUBLISHERS = 5;
    public static final int MAX_SUBSCRIBERS = 10;
    private final String ip;
    private final int port;
    private int numPublishers = 0;
    private int numSubscribers = 0;
    public BrokerInfo(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }
    public String getIp() {
        return ip;
    }
    public int getPort() {
        return port;
    }
    public boolean hasRoomForPublishers() {
        return numPublishers < MAX_PUBLISHERS;
    }
    public boolean hasRoomForSubscribers() {
        return numSubscribers < MAX_SUBSCRIBERS;
    }
    public void addPublisher() {
        // adding publisher logic
        numPublishers++;
    }
    public void addSubscriber() {
        // adding subscriber logic
        numSubscribers++;
    }
}
