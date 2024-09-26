package directory;

import Shared.IBroker;
import Shared.IDirectory;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class Directory extends UnicastRemoteObject implements IDirectory {
    private static Directory directory;
    private Registry registry;
//    private BrokerFactory bf;
    private final String ip;
    private final int port;
    private int brokerCount;
    private final List<IBroker> brokers;
    public static Directory getInstance() {
        return directory;
    }
    public static void init(String ip, int port, Registry registry) throws RemoteException {
        if (directory != null) return;
        directory = new Directory(ip, port, registry);
    }
    private Directory(String ip, int port, Registry registry) throws RemoteException {
        this.ip = ip;
        this.port = port;
        brokers = new ArrayList<>();
        brokerCount = 0;
        this.registry = registry;
        String directoryIdentifier = "Directory";
        try {
            registry.bind(directoryIdentifier, this);
        } catch (AlreadyBoundException e) {
            try {
                registry.unbind(directoryIdentifier);
                registry.bind(directoryIdentifier, this);
            }
            catch (AlreadyBoundException | NotBoundException er) {}
        }
//        try {
//            bf = new BrokerFactory(registry);
//        } catch (RemoteException e) {
//            throw new RuntimeException(e);
//        }
    }

//    public Registry

    @Override
    public void addBroker(String id) throws RemoteException {
        IBroker b;
        try {
            b = (IBroker) registry.lookup(id);
        }
        catch (NotBoundException e) {
            System.out.println("Broker with id " + id + " doesn't exist.");
            return;
        }
        // connect new broker to all other brokers
        for (IBroker b2 : brokers) {
            b.addBroker(b2);
            b2.addBroker(b);
        }
        brokers.add(b);
        brokerCount++;
    }
//    // TODO: I'm going to have to make sure this is all synchronized - might be a use for semaphores?? idk
//    // alternatively just
//    public void connectToBroker() throws NoSuchElementException {
//        IBroker b = getMostAvailableBroker();
//        // TODO: do other stuff
//    }
    public IBroker getMostAvailableBroker() {
        if (brokers.isEmpty()) {
            throw new NoSuchElementException("There are currently no brokers connected to the network!");
        }
        IBroker b = brokers.get(0);
        for (int i = 1; i < brokers.size(); i++) {
            if (brokers.get(i).getNumConnections() < b.getNumConnections()) b = brokers.get(i);
        }
        return b;
    }

    @Override
    public void addPublisher(String username) throws AlreadyBoundException, RemoteException {
        IBroker b = getMostAvailableBroker();
        b.addPublisher(username);
    }

    @Override
    public void addSubscriber(String username) throws AlreadyBoundException, RemoteException {
        IBroker b = getMostAvailableBroker();
        b.addSubscriber(username);
    }

}
