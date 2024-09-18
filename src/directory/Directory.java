package directory;

import java.util.ArrayList;
import java.util.List;

public class Directory {
    private final String ip;
    private final int port;
    private final List<BrokerInfo> brokers;
    public Directory(String ip, int port) {
        this.ip = ip;
        this.port = port;
        brokers = new ArrayList<>();
    }
    public void addBroker(String ip, int port) {
        brokers.add(new BrokerInfo(ip, port));
    }
    // TODO: I'm going to have to make sure this is all synchronized - might be a use for semaphores?? idk
    // alternatively just
    public void connectToBroker() {

    }
}
