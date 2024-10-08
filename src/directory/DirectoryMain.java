package directory;

import Shared.InputVerifier;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class DirectoryMain {
    private static Directory directory;
    private static final int numArgs = 4;
    private final static String USAGE_MESSAGE = "Usage: java -jar directory.jar " +
            "{rmi_ip} {rmi_port} {directory_ip} {directory_port}";
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
        }
        catch (RemoteException e) {
            System.out.println("Error creating RMI registry");
            return;
        }
        // TODO: seems like it doesn't automatically terminate when the RMI registry shuts? random...
        try {
            Directory.init(directoryIP, directoryPort, registry);
        }
        catch (RemoteException e) {
            System.out.println("RMI issue caused directory creation to fail; please try again later.");
            System.exit(1);
            return;
        }
        System.out.println("Directory is now running");
        directory = Directory.getInstance();
    }
}
