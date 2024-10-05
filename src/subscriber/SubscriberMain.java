package subscriber;

import Shared.*;

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

public class SubscriberMain {
    final static int NUM_ARGS = 5;
    final static String USAGE_MESSAGE = "java -jar subscriber.jar " +
            "username registry_ip registry_port directory_ip directory_port ";
    final static String COMMAND_LIST = "Commands:\n" +
            SubscriberCommand.getSubscriberCommandUsage() + GlobalCommand.getGlobalCommandUsage();
    final static InputVerifier v = new InputVerifier();
    public static void main(String[] args) {
        int directoryPort, registryPort;
        // Verify command line args
        try {
            registryPort = v.verifyPort(args, 2, NUM_ARGS, USAGE_MESSAGE);
//            directoryPort = v.verifyPort(args, 4, NUM_ARGS, USAGE_MESSAGE);
        }
        catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        }
        ISubscriber subscriber;
        String username = args[0];
        String registryIP = args[1];
//        String directoryIP = args[3];
        Socket s;
        try {
            Registry registry = LocateRegistry.getRegistry(registryIP, registryPort);
            IDirectory d = (IDirectory) registry.lookup(Messenger.DIRECTORY_RMI_NAME);
            IBroker broker = d.getMostAvailableBroker();
            System.out.println(broker.getId());
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
//            broker.addSubscriber(s, username);
//            d.addSubscriber(username);
//            ISubscriberFactory sf = (ISubscriberFactory) registry.lookup("SubscriberFactory");
//            sf.createSubscriber(args[0]);
            // TODO: make this more robust (rather than just waiting an arbitrary amount of time)
            // there is some delay in the new subscriber object actually being bound
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
                return;
            }
        }
    }
    private static boolean handleInput(ISubscriber subscriber, String[] input) {
        try {
            if (subscriber.hasMessage()) {
                System.out.println(subscriber.getMessage());
                // TODO: this might not actually update D: - so tbh probably need to use some sort of lock
                while (subscriber.hasMessage()) {/* wait so it doesn't print a million times*/}
            }
        }
        catch (RemoteException e) {
            // TODO - this might actually deal with one of the disconnection issues?!
            System.out.println(e.getMessage());
        }
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
                    int id = v.verifyTopicId(input, 1, 2, "Usage: sub {topic_id}");
                    subscriber.subscribeToTopic(id);
                    // todo - would be nice to display the topic name as well here if possible!
                    System.out.println("Subscribed to " + id);
                } catch (NoSuchElementException e) {
                    System.out.println(e.getMessage());
                }
            }
            else if (command.equals(SubscriberCommand.CURRENT.toString())) {
                System.out.println(subscriber.showCurrentSubscriptions());
            }
            else if (command.equals(SubscriberCommand.UNSUB.toString())) {
                try {
                    int id = v.verifyTopicId(input, 1, 2, "Usage: unsub {topic_id}");
                    subscriber.unsubscribe(id);
                    // TODO - would be nice to show the topic name here!
                    System.out.println("Unsubscribed from " + id);
                } catch (IllegalArgumentException e) {
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
//        switch (input[0]) {
//            case "h":
//            case "help":
//                System.out.println(COMMAND_LIST);
//                break;
//            // close connection
//            case "disconnect":
//            case "d":
//            case "dc":
//                System.out.println("Logging out");
//                return false;
//            case "list":
//                subscriber.listAllAvailableTopics();
//                break;
//            case "sub":
//                try {
//                    int id = verifyTopicId(input, 1, 2, "Usage: sub {topic_id}");
//                    subscriber.subscribeToTopic(id);
//                }
//                catch (IllegalArgumentException e) {
//                    System.out.println(e.getMessage());
//                }
//                break;
//            case "current":
//                subscriber.showCurrentSubscriptions();
//                break;
//            case "unsub":
//                try {
//                    int id = verifyTopicId(input, 1, 2, "Usage: unsub {topic_id}");
//                    subscriber.unsubscribe(id);
//                }
//                catch (IllegalArgumentException e) {
//                    System.out.println(e.getMessage());
//                }
//                break;
//            default:
//                System.out.println("Unrecognised command. Press \"h\" for a list of commands" + ".");
//        }
//        return true;
    }
}
