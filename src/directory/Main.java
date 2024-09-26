package directory;

import Shared.InputVerifier;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main {
    private static Directory directory;
    private final static String USAGE_MESSAGE = "Usage: java -jar directory.jar directory_ip directory_port";
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println(USAGE_MESSAGE);
        }
        int port;
        try {
            port = new InputVerifier().verifyPort(args, 1, 2, USAGE_MESSAGE);
        }
        catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        }
        // TODO: I think this will need to happen in the broker class now? I.e. each broker has its own factory
        Registry registry;
        try {
            registry = LocateRegistry.getRegistry(args[0], port);
//            SubscriberFactory sf = new SubscriberFactory(registry);
//            registry.bind("SubscriberFactory", sf);
//            PublisherFactory pf = new PublisherFactory(registry);
//            registry.bind("PublisherFactory", pf);
//            BrokerFactory bf = new BrokerFactory(registry);
//            registry.bind("BrokerFactory", bf);
        }
        catch (RemoteException e) {
            System.out.println("Error creating RMI registry");
            return;
        }
//        catch (AlreadyBoundException e) {
//            System.out.println("Object " + e.getMessage() + "is already bound!");
//            return;
//        }
        // TODO: seems like it doesn't automatically terminate when the RMI registry shuts? random...
        try {
            Directory.init("localhost", port, registry);
        }
        catch (RemoteException e) {
            System.out.println("Issue starting directory;");
            return;
        }
        System.out.println("Directory is now running");
//        directory = Directory.getInstance();
//        while (true) {
//            // TODO: check for incoming brokers, publishers, subscribers, and allocate them accordingly
//
//            break;
//        }
    }
}
