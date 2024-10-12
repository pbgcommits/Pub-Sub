package directory;

import shared.util.InputVerifier;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Load the directory into the RMI registry.
 * The directory will dynamically connect brokers to publishers, subscribers, and each other.
 * @author Patrick Barton Grace 1557198
 */
public class DirectoryMain {
    private static final int numArgs = 2;
    private final static String USAGE_MESSAGE = "Usage: java -jar directory.jar " +
            "{rmi_ip} {rmi_port}";
    public static void main(String[] args) {
        if (args.length != numArgs) {
            System.out.println(USAGE_MESSAGE);
        }
        // Make sure command line args are used correctly
        int rmiPort;
        try {
            InputVerifier verifier = new InputVerifier();
            rmiPort = verifier.verifyPort(args, 1, numArgs, USAGE_MESSAGE);
        }
        catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        }
        String rmiIP = args[0];
        // Find the RMI registry
        Registry registry;
        try {
            registry = LocateRegistry.getRegistry(rmiIP, rmiPort);
        }
        catch (RemoteException e) {
            System.out.println("Error creating RMI registry");
            return;
        }
        // Load the Directory object, which will handle connecting things with brokers
        try {
            Directory.init(registry);
        }
        catch (RemoteException e) {
            System.out.println("RMI issue caused directory creation to fail; please try again later.");
            e.printStackTrace();
            System.exit(1);
            return;
        }
        System.out.println("Directory is now running");
    }
}
