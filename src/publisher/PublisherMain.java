package publisher;

import Shared.*;

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
            System.out.println(broker.getId());
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
            System.out.println("Oh deary dar intterupted");
            return;
        }
        System.out.println("Welcome, " + username);
        System.out.println(COMMAND_LIST);
        Scanner scanner = new Scanner(System.in);
        new PubServerConnection(s).start();
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
                // TODO : read in space separated message D:
                int id = v.verifyTopicId(input, 1, 3, PublisherCommand.PUBLISH.getUsage());
                publisher.publish(id, input[2]);
                System.out.println("Successfully published message");
            }
            catch (RemoteException | IllegalArgumentException | NoSuchElementException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
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
    }
}
