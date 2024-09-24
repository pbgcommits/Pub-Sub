package directory;

import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class Directory {
    private static Directory directory;
    private Registry registry;
    private final String ip;
    private final int port;
    private final List<BrokerInfo> brokers;
    public static Directory getInstance() {
        return directory;
    }
    public static void init(String ip, int port, Registry registry) {
        if (directory != null) return;
        directory = new Directory(ip, port, registry);
    }
    private Directory(String ip, int port, Registry registry) {
        this.ip = ip;
        this.port = port;
        brokers = new ArrayList<>();
        this.registry = registry;
    }
    public void addBroker(String ip, int port) {
        brokers.add(new BrokerInfo(ip, port));
    }
    // TODO: I'm going to have to make sure this is all synchronized - might be a use for semaphores?? idk
    // alternatively just
    public void connectToBroker() {

    }
}
