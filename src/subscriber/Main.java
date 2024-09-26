package subscriber;

import Shared.*;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Main {
    final static String USAGE_MESSAGE = "java -jar subscriber.jar username broker_ip broker_port";
    final static String COMMAND_LIST = "Commands:\n" +
            SubscriberCommand.getSubscriberCommandUsage() + GlobalCommand.getGlobalCommandUsage();
    final static InputVerifier v = new InputVerifier();
    public static void main(String[] args) {
        int port;
        try {
            port = v.verifyPort(args, 2, 3, USAGE_MESSAGE);
        }
        catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        }
        ISubscriber subscriber;
        String username = args[0];
        String ip = args[1];
        try {
            Registry registry = LocateRegistry.getRegistry(ip, port);
            IDirectory d = (IDirectory) registry.lookup("Directory");
            d.addSubscriber(username);
//            ISubscriberFactory sf = (ISubscriberFactory) registry.lookup("SubscriberFactory");
//            sf.createSubscriber(args[0]);
            subscriber = (ISubscriber) registry.lookup(username);
        }
        catch (AlreadyBoundException | RemoteException | NotBoundException e) {
            System.out.println(e.getMessage());
            return;
        }
        System.out.println("Welcome, " + username + "." + " (ip: " + ip + ", port: " + port + ")");
        System.out.println("Available commands:");
        System.out.println(COMMAND_LIST);
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
                System.out.println(subscriber.listAllAvailableTopics());
            } else if (command.equals(SubscriberCommand.SUB.toString())) {
                try {
                    int id = v.verifyTopicId(input, 1, 2, "Usage: sub {topic_id}");
                    subscriber.subscribeToTopic(id);
                    // todo - would be nice to display the topic name as well here if possible!
                    System.out.println("Subscribed to " + id);
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            } else if (command.equals(SubscriberCommand.CURRENT.toString())) {
                System.out.println(subscriber.showCurrentSubscriptions());
            } else if (command.equals(SubscriberCommand.UNSUB.toString())) {
                try {
                    int id = v.verifyTopicId(input, 1, 2, "Usage: unsub {topic_id}");
                    subscriber.unsubscribe(id);
                    // TODO - would be nice to show the topic name here!
                    System.out.println("Unsubscribed from " + id);
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            } else {
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
