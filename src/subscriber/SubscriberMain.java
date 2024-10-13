package subscriber;

import shared.commands.GlobalCommand;
import shared.remote.IBroker;
import shared.remote.IDirectory;
import shared.remote.ISubscriber;
import shared.util.InputVerifier;
import shared.util.Messenger;

import javax.net.SocketFactory;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Initialises a subscriber client.
 * @author Patrick Barton Grace 1557198
 */
public class SubscriberMain {
    final static int NUM_ARGS = 3;
    final static String USAGE_MESSAGE = "java -jar subscriber.jar " +
            "username registry_ip registry_port";
    final static String COMMAND_LIST = "Commands:\n" +
            SubscriberCommand.getSubscriberCommandUsage() + GlobalCommand.getGlobalCommandUsage();
    final static InputVerifier v = new InputVerifier();
    public static void main(String[] args) {
        int registryPort;
        // Verify command line args
        try {
            registryPort = v.verifyPort(args, 2, NUM_ARGS, USAGE_MESSAGE);
        }
        catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        }
        ISubscriber subscriber;
        String username = args[0];
        String registryIP = args[1];
        Socket s;
        // Create a subscriber object on the server
        try {
            Registry registry = LocateRegistry.getRegistry(registryIP, registryPort);
            IDirectory d = (IDirectory) registry.lookup(Messenger.DIRECTORY_RMI_NAME);
            IBroker broker = d.getMostAvailableBroker();
            System.out.println("Connecting to: " + broker.getId());
            // Send a message to the broker telling it to create a new "Subscriber" object and export it to RMI
            try {
                SocketFactory sf = SocketFactory.getDefault();
                s = sf.createSocket(broker.getIp(), broker.getPort());
                DataOutputStream output = new DataOutputStream(s.getOutputStream());
                output.writeUTF(new Messenger().newSubscriberMessage(username));
            }
            catch (IOException e) {
                System.out.println("Couldn't connect to the broker.");
                System.out.println(e.getMessage());
                return;
            }
            // Give the server time to create the serverside subscriber
            Thread.sleep(1000);
            subscriber = (ISubscriber) registry.lookup(username);
        }
        catch (NotBoundException e) {
            System.out.println("There are currently no brokers connected to the network. Please try again later.");
            return;
        }
        catch (RemoteException e) {
            System.out.println("Issue connecting to either broker or directory or registry, please try again later.");
            return;
        }
        catch (InterruptedException e) {
            System.out.println("Very sad issue :(");
            return;
        }

        // Maintains socket connection with broker
        new SubServerConnection(s).start();

        System.out.println("Welcome, " + username);
        System.out.println("Available commands:");
        System.out.println(COMMAND_LIST);
        // Get input from user
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String[] input = scanner.nextLine().split(" ");
            if (!handleInput(subscriber, input)) {
                System.exit(0);
                return;
            }
        }
    }
    /**
     * Read in user commands and attempt to execute them.
     * @param subscriber The remote subscriber object which commands should be called upon.
     * @param input The user's input.
     * @return Whether the user wishes to continue running the program.
     */
    private static boolean handleInput(ISubscriber subscriber, String[] input) {
//        try {
//            if (subscriber.hasMessage()) {
//                System.out.println(subscriber.getMessage());
//                // TODO: this might not actually update D: - so tbh probably need to use some sort of lock
//                while (subscriber.hasMessage()) {/* wait so it doesn't print a million times*/}
//            }
//        }
//        catch (RemoteException e) {
//            // TODO - this might actually deal with one of the disconnection issues?!
//            System.out.println(e.getMessage());
//        }
        String command = input[0];
        for (String h : GlobalCommand.HELP.getOptions()) {
            if (command.equals(h)) {
                System.out.println(COMMAND_LIST);
                return true;
            }
        }
        for (String d : GlobalCommand.DISCONNECT.getOptions()) {
            if (command.equals(d)) {
                System.out.println("Logging out");
                return false;
            }
        }
        try {
            if (command.equals(SubscriberCommand.LIST.toString())) {
                String allTopics = subscriber.listAllAvailableTopics();
                if (allTopics.equals("")) System.out.println("No topics currently available.");
                else System.out.println(allTopics);
            }
            else if (command.equals(SubscriberCommand.SUB.toString())) {
                try {
                    if (input.length != 2) throw new IllegalArgumentException("Usage: " + SubscriberCommand.SUB.getUsage());
                    String id = input[1];
                    subscriber.subscribeToTopic(id);
                    // todo - would be nice to display the topic name as well here if possible!
                    System.out.println("Subscribed to " + id);
                }
                catch (NoSuchElementException | IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }
            else if (command.equals(SubscriberCommand.CURRENT.toString())) {
                String curSubs = subscriber.showCurrentSubscriptions();
                if (curSubs.equals("")) {
                    System.out.println("Currently not subscribed to any topics. Subscribe using: " + SubscriberCommand.SUB.getUsage());
                }
                else {
                    System.out.println(curSubs);
                }
            }
            else if (command.equals(SubscriberCommand.UNSUB.toString())) {
                try {
                    if (input.length != 2) throw new IllegalArgumentException("Usage: " + SubscriberCommand.SUB.getUsage());
                    String id = input[1];
                    subscriber.unsubscribe(id);
                    // TODO - would be nice to show the topic name here!
                    System.out.println("Unsubscribed from " + id);
                }
                catch (IllegalArgumentException | NoSuchElementException e) {
                    System.out.println(e.getMessage());
                }
            }
            else {
                System.out.println("Unrecognised command. Press \"h\" for a list of commands" + ".");
            }
        }
        catch (RemoteException e) {
            System.out.println(e.getMessage());
        }
        return true;
    }
}
