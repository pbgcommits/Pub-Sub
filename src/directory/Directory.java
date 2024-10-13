package directory;

import shared.remote.IBroker;
import shared.remote.IDirectory;
import shared.util.Messenger;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * The Directory fields requests from publishers, subscribers, and brokers to connect to each other.
 * @author Patrick Barton Grace 1557198
 */
public class Directory extends UnicastRemoteObject implements IDirectory {
    private static Directory directory;
    /** The RMI registry where objects will be stored. */
    private Registry registry;
    private int brokerCount;
    private final List<IBroker> brokers;
    public static Directory getInstance() {
        return directory;
    }
    public static void init(Registry registry) throws RemoteException {
        if (directory != null) return;
        directory = new Directory(registry);
    }
    private Directory(Registry registry) throws RemoteException {
        brokers = new ArrayList<>();
        brokerCount = 0;
        this.registry = registry;
        // Add the directory to the server.
        try {
            registry.bind(Messenger.DIRECTORY_RMI_NAME, this);
        } catch (AlreadyBoundException e) {
            System.out.println("Resetting directory");
            // reset directory
            for (String s : registry.list()) {
                try {
                    registry.unbind(s);
                }
                catch (NotBoundException ex) {
                    System.out.println("unbinding issue");
                }
            }
            registry.rebind(Messenger.DIRECTORY_RMI_NAME, this);
        }
    }

    /**
     * Connects a broker to all other brokers already in the network.
     * @param id The new broker's id to be added.
     * @throws RemoteException
     */
    @Override
    public synchronized void addBroker(String id) throws RemoteException {
        IBroker b;
        // The broker should have added itself to the RMI registry
        try {
            b = (IBroker) registry.lookup(id);
        }
        catch (NotBoundException e) {
            System.out.println("Broker with id " + id + " doesn't exist.");
            return;
        }
        // Connect new broker to all other brokers
        Iterator<IBroker> iter = brokers.listIterator();
        while (iter.hasNext()) {
            IBroker b2 = iter.next();
            try {
                // Order is important; otherwise b may add a disconnected broker (b2)
                b2.addBroker(b);
                System.out.println(b2.getId());
                b.addBroker(b2);
            }
            catch (RemoteException e) {
                System.out.println("Removing disconnected broker");
                iter.remove();
                brokerCount--;
            }
        }
        brokers.add(b);
        brokerCount++;
        System.out.println("Added broker with id " + id);
    }

    /**
     * Queries each broker to see how many subscribers/publishers are currently connected to it.
     * @return The broker with the fewest active connections.
     * @throws NoSuchElementException If there are currently no brokers online.
     * @throws RemoteException If there is an issue connecting to the RMI registry.
     */
    @Override
    public synchronized IBroker getMostAvailableBroker() throws NoSuchElementException, RemoteException {
        System.out.println("Somebody has requested an available broker");
        if (brokers.isEmpty()) {
            throw new NoSuchElementException("There are currently no brokers connected to the network!");
        }
        IBroker b = brokers.get(0);
        int minConnections = Integer.MAX_VALUE;
        try {
            minConnections = b.getNumConnections();
            for (int i = 1; i < brokers.size(); i++) {
                int newConnections = brokers.get(i).getNumConnections();
                if (newConnections < minConnections) {
                    b = brokers.get(i);
                    minConnections = newConnections;
                }
            }
        }
        catch (RemoteException e) {
            System.out.println("COULDN'T CONNECT TO A BROKER");
//            e.printStackTrace();
        }
        System.out.println("Most available broker is " + b.getId() + " with " + minConnections + " connections.");
        return b;
    }


}
