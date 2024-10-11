package publisher;

import shared.*;

import javax.naming.LimitExceededException;
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

public class PublisherMain {
    final static int NUM_ARGS = 3;
    final static String USAGE_MESSAGE = "java -jar publisher.jar " +
            "username registry_ip registry_port";
    final static String COMMAND_LIST = "Available commands:\n" +
            PublisherCommand.getPublisherCommandUsage() + GlobalCommand.getGlobalCommandUsage();
    final static InputVerifier v = new InputVerifier();
    public static void main(String[] args) {
        int registryPort, directoryPort;
        // Verify command line args
        try {
            registryPort = v.verifyPort(args, 2, NUM_ARGS, USAGE_MESSAGE);
        }
        catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        }
        IPublisher publisher;
        String username = args[0];
        String registryIP = args[1];
        Socket s;
        try {
            Registry registry = LocateRegistry.getRegistry(registryIP, registryPort);
            IDirectory directory = (IDirectory) registry.lookup("Directory");
            IBroker broker = directory.getMostAvailableBroker();
            System.out.println("Connected to: " + broker.getId());
            try {
                SocketFactory sf = SocketFactory.getDefault();
                s = sf.createSocket(broker.getIp(), broker.getPort());
                DataOutputStream output = new DataOutputStream(s.getOutputStream());
                output.writeUTF(new Messenger().newPublisherMessage(username));
                output.flush();
            }
            catch (IOException e) {
                System.out.println("Couldn't connect to the broker!");
                System.out.println(e.getMessage());
                return;
            }
            Thread.sleep(1000);
            publisher = (IPublisher) registry.lookup(username);
        }
        catch (RemoteException | NoSuchElementException | NotBoundException e) {
            System.out.println(e.getMessage());
            return;
        }
        catch (InterruptedException e) {
            System.out.println("Oh deary dear interrupted");
            return;
        }
        System.out.println("Welcome, " + username);
        System.out.println(COMMAND_LIST);
        Scanner scanner = new Scanner(System.in);
        new PubServerConnection(s).start();
        while (true) {
            String[] input = scanner.nextLine().split(" ");
            if (!handleInput(publisher, input)) {
                System.exit(0);
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
                if (input.length < 3) throw new IllegalArgumentException("Usage: " + PublisherCommand.CREATE.getUsage());
                StringBuilder topicName = new StringBuilder();
                topicName.append(input[2]);
                for (int i = 3; i < input.length; i++) {
                    topicName.append(" ");
                    topicName.append(input[i]);
                }
                String id = publisher.createNewTopic(topicName.toString(), input[1]);
                System.out.println("Created new topic: " + topicName.toString() + " with id " + id);
            }
            catch (RemoteException | IllegalArgumentException | LimitExceededException e) {
                System.out.println(e.getMessage());
            }
        }
        else if (command.equals(PublisherCommand.PUBLISH.toString())) {
            try {
                // TODO : read in space separated message D:
//                int id = v.verifyTopicId(input, 1, -1, GlobalCommand.PublisherCommand.PUBLISH.getUsage());
                if (input.length < 3) throw new IllegalArgumentException("Usage: " + PublisherCommand.PUBLISH.getUsage());
                String id = input[1];
                StringBuilder message = new StringBuilder();
                message.append(input[2]);
                for (int i = 3; i < input.length; i++) {
                    message.append(" ");
                    message.append(input[i]);
                }
                publisher.publish(id, message.toString());
                System.out.println("Successfully published message");
            }
            catch (RemoteException | IllegalArgumentException | NoSuchElementException e) {
                System.out.println(e.getMessage());
            }
        }
        else if (command.equals(PublisherCommand.SHOW.toString())) {
            try {
//                int id = v.verifyTopicId(input, 1, 2, GlobalCommand.PublisherCommand.SHOW.getUsage());
                if (input.length != 2) throw new IllegalArgumentException("Usage: " + PublisherCommand.SHOW.getUsage());
                String id = input[1];
                System.out.println(publisher.show(id));
            }
            catch (RemoteException | IllegalArgumentException | NoSuchElementException e) {
                System.out.println(e.getMessage());
            }
        }
        else if (command.equals(PublisherCommand.DELETE.toString())) {
            try {
//                int id = v.verifyTopicId(input, 1, 2, GlobalCommand.PublisherCommand.DELETE.getUsage());
                if (input.length != 2) throw new IllegalArgumentException("Usage: " + PublisherCommand.DELETE.getUsage());
                String id = input[1];
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
    }
}
