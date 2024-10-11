package directory;

import shared.IBroker;
import shared.IDirectory;
import shared.Messenger;

import java.net.Socket;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class Directory extends UnicastRemoteObject implements IDirectory {
    private static Directory directory;
    private Registry registry;
    private int brokerCount;
    private final List<IBroker> brokers;
    public static Directory getInstance() {
        return directory;
    }
    public static void init( Registry registry) throws RemoteException {
        if (directory != null) return;
        directory = new Directory(registry);
    }
    private Directory(Registry registry) throws RemoteException {
        brokers = new ArrayList<>();
        brokerCount = 0;
        this.registry = registry;
        try {
            registry.bind(Messenger.DIRECTORY_RMI_NAME, this);
        } catch (AlreadyBoundException e) {
            System.out.println("Resetting directory");
            registry.rebind(Messenger.DIRECTORY_RMI_NAME, this);
        }
    }

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
        Iterator<IBroker> iter = brokers.listIterator();
        while (iter.hasNext()) {
            IBroker b2 = iter.next();
            try {
                // order is important; otherwise b may add a disconnected broker (b2)
                b2.addBroker(b);
                System.out.println(b2.getId());
                b.addBroker(b2);
            }
            catch (RemoteException e) {
                System.out.println("Removing disconnected broker");
                iter.remove();
            }
        }
        brokers.add(b);
        brokerCount++;
        System.out.println("Added broker with id " + id);
    }
    @Override
    public IBroker getMostAvailableBroker() throws NoSuchElementException, RemoteException {
        System.out.println("Somebody has requested an available broker");
        if (brokers.isEmpty()) {
            throw new NoSuchElementException("There are currently no brokers connected to the network!");
        }
        IBroker b = brokers.get(0);
        try {
            for (int i = 1; i < brokers.size(); i++) {
                if (brokers.get(i).getNumConnections() < b.getNumConnections()) b = brokers.get(i);
            }
        }
        catch (RemoteException e) {
            System.out.println("COULDN'T CONNECT TO A BROKER");
//            e.printStackTrace();
        }
        return b;
    }

//    @Override
    public void addPublisher(Socket client, String username) throws NoSuchElementException, AlreadyBoundException, RemoteException {
        IBroker b = getMostAvailableBroker();
        b.addPublisher(client, username);
    }

//    @Override
    public void addSubscriber(Socket client, String username) throws NoSuchElementException, AlreadyBoundException, RemoteException {
        IBroker b = getMostAvailableBroker();
        System.out.println("Adding subscriber to " + b.getId());
        b.addSubscriber(client, username);
    }

}
