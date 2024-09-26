package publisher;

import Shared.*;

import javax.naming.LimitExceededException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    final static String USAGE_MESSAGE = "java -jar publisher.jar username broker_ip broker_port";
    final static String COMMAND_LIST = "Available commands:\n" +
            PublisherCommand.getPublisherCommandUsage() + GlobalCommand.getGlobalCommandUsage();
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
        IPublisher publisher;
        String username = args[0];
        String ip = args[1];
        try {
            Registry registry = LocateRegistry.getRegistry(ip, port);
            IDirectory directory = (IDirectory) registry.lookup("Directory");
            directory.addPublisher(username);
//            IPublisherFactory pf = (IPublisherFactory) registry.lookup("PublisherFactory");
//            pf.createPublisher(args[0]);
            publisher = (IPublisher) registry.lookup(username);
        }
        catch (AlreadyBoundException | RemoteException | NotBoundException e) {
            System.out.println(e);
            System.out.println(e.getMessage());
            return;
        }
        System.out.println("Welcome, " + username + "." + " (ip: " + ip + ", port: " + port + ")");
        System.out.println(COMMAND_LIST);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String[] input = scanner.nextLine().split(" ");
            if (!handleInput(publisher, input)) {
                return;
            }
        }
    }
    private static boolean handleInput(IPublisher publisher, String[] input) {
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
        if (command.equals(PublisherCommand.CREATE.toString())) {
            try {
                // TODO: it would be nice if you could make a name with more than one word? but probably too annoying...
                if (input.length != 2) throw new IllegalArgumentException("Usage: " + PublisherCommand.CREATE.getUsage());
                int id = publisher.createNewTopic(input[1]);
                System.out.println("Created new topic: " + input[1] + " with id " + id);
            }
            catch (RemoteException | IllegalArgumentException | LimitExceededException e) {
                System.out.println(e.getMessage());
            }
        }
        else if (command.equals(PublisherCommand.PUBLISH.toString())) {
            try {
                int id = v.verifyTopicId(input, 1, 3, PublisherCommand.PUBLISH.getUsage());
                publisher.publish(id, input[2]);
                System.out.println("Successfully published message");
            }
            catch (RemoteException | IllegalArgumentException | NoSuchElementException e) {
                System.out.println(e.getMessage());
            }
        }
        else if (command.equals(PublisherCommand.SHOW.toString())) {
            try {
                int id = v.verifyTopicId(input, 1, 2, PublisherCommand.SHOW.getUsage());
                System.out.println(publisher.show(id));
            }
            catch (RemoteException | IllegalArgumentException | NoSuchElementException e) {
                System.out.println(e.getMessage());
            }
        }
        else if (command.equals(PublisherCommand.DELETE.toString())) {
            try {
                int id = v.verifyTopicId(input, 1, 2, PublisherCommand.DELETE.getUsage());
                publisher.delete(id);
                System.out.println("Successfully deleted topic with id " + id);
            }
            catch (RemoteException | IllegalArgumentException | NoSuchElementException e) {
                System.out.println(e.getMessage());
            }
        }
        else {
            System.out.println("Unrecognised command. Press \"h\" for a list of commands" + ".");
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
//            case "create":
//                try {
////                    int id = verifyTopicId(input, 1, 3, PublishCommand.CREATE.getUsage());
////                    publisher.createNewTopic(id, input[2]);
//                    // TODO: it would be nice if you could make a name with more than one word? but probably too annoying...
//                    if (input.length != 2) throw new IllegalArgumentException("Usage: " + PublisherCommand.CREATE.getUsage());
//                    int id = publisher.createNewTopic(input[1]);
//                    System.out.println("Created new topic: " + input[1] + " with id " + id);
//                }
//                catch (IllegalArgumentException | LimitExceededException e) {
//                    System.out.println(e.getMessage());
//                }
//                break;
//            case "publish":
//                try {
//                    int id = verifyTopicId(input, 1, 3, PublisherCommand.PUBLISH.getUsage());
//                    publisher.publish(id, input[2]);
//                    System.out.println("Successfully published message");
//                }
//                catch (IllegalArgumentException | NoSuchElementException e) {
//                    System.out.println(e.getMessage());
//                }
//                break;
//            case "show":
//                try {
//                    int id = verifyTopicId(input, 1, 2, PublisherCommand.SHOW.getUsage());
//                    System.out.println(publisher.show(id));
//                }
//                catch (IllegalArgumentException | NoSuchElementException e) {
//                    System.out.println(e.getMessage());
//                }
//                break;
//            case "delete":
//                try {
//                    int id = verifyTopicId(input, 1, 2, PublisherCommand.DELETE.getUsage());
//                    publisher.delete(id);
//                    System.out.println("Successfully deleted topic with id " + id);
//                }
//                catch (IllegalArgumentException | NoSuchElementException e) {
//                    System.out.println(e.getMessage());
//                }
//                break;
//            default:
//                System.out.println("Unrecognised command. Press \"h\" for a list of commands" + ".");
//        }
//        return true;
    }
}
