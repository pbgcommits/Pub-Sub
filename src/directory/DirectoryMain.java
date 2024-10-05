package directory;

import Shared.InputVerifier;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class DirectoryMain {
    private static Directory directory;
    private static final int numArgs = 4;
    private final static String USAGE_MESSAGE = "Usage: java -jar directory.jar rmi_ip rmi_port directory_ip directory_port";
    public static void main(String[] args) {
        if (args.length != numArgs) {
            System.out.println(USAGE_MESSAGE);
        }
        int rmiPort, directoryPort;
        try {
            InputVerifier verifier = new InputVerifier();
            rmiPort = verifier.verifyPort(args, 1, numArgs, USAGE_MESSAGE);
            directoryPort = verifier.verifyPort(args, 3, numArgs, USAGE_MESSAGE);
        }
        catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        }
        // TODO: will need to pass both of these into every other jar also :))))
        String rmiIP = args[0];
        String directoryIP = args[2];
        // TODO: I think this will need to happen in the broker class now? I.e. each broker has its own factory
        Registry registry;
        try {
            registry = LocateRegistry.getRegistry(rmiIP, rmiPort);
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
            Directory.init(directoryIP, directoryPort, registry);
        }
        catch (RemoteException e) {
            System.out.println("Issue starting directory");
            System.exit(1);
            return;
        }
        System.out.println("Directory is now running");
        directory = Directory.getInstance();
//        ServerSocketFactory serverSocketFactory = ServerSocketFactory.getDefault();
//        int brokerCount = 0;
//        try (ServerSocket serverSocket = serverSocketFactory.createServerSocket(directoryPort)) {
//            while (true) {
//                // TODO: check for incoming brokers, publishers, subscribers, and allocate them accordingly
//                Socket client = serverSocket.accept();
////                brokerCount++;
//                new Connector(client, directory).start();
////                DataInputStream d = new DataInputStream(client.getInputStream());
////                String input = d.readUTF();
////                Messenger m = new Messenger();
////                String connectionType = m.getConnectionType(input);
////                String username = m.getUsername(input);
////                if (connectionType.equals(m.subscriber())) {
////                    directory.addSubscriber(client, username);
////                } else if (connectionType.equals(m.publisher())) {
////                    directory.addPublisher(client, username);
////                }
//            }
//        }
//        catch (IOException e) {
//            System.out.println("issue w something");
//            System.out.println(e.getMessage());
//            e.printStackTrace();
//        }
    }
}
