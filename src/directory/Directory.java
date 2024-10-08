package directory;

import Shared.IBroker;
import Shared.IDirectory;
import Shared.Messenger;

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
//    private BrokerFactory bf;
//    private final String ip;
//    private final int port;
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
//        this.ip = ip;
//        this.port = port;
        brokers = new ArrayList<>();
        brokerCount = 0;
        this.registry = registry;
        try {
            registry.bind(Messenger.DIRECTORY_RMI_NAME, this);
        } catch (AlreadyBoundException e) {
            System.out.println("Resetting directory");
            registry.rebind(Messenger.DIRECTORY_RMI_NAME, this);
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
        /** TODO  This code BREAKS if a broker disconnects from the system!!!
         * TODO i might have fixed it with the try block in the for loop
         * TODO okay i didnt but atl east i tried :)*/
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
                b.addBroker(b2);
            }
            catch (RemoteException e) {
                iter.remove();
            }
        }
        brokers.add(b);
        brokerCount++;
        System.out.println("Added broker with id " + id);
    }
//    // TODO: I'm going to have to make sure this is all synchronized - might be a use for semaphores?? idk
//    // alternatively just
//    public void connectToBroker() throws NoSuchElementException {
//        IBroker b = getMostAvailableBroker();
//        // TODO: do other stuff
//    }
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
