package subscriber;

import Shared.PortVerifier;

import java.util.Scanner;

public class Main {
    final static String USAGE_MESSAGE = "Usage: java -jar subscriber.jar username broker_ip broker_port";
    final static String COMMAND_LIST = "Commands:" +
            "\nh/help: show this list of commands" +
            "\nlist: show all available topics to be subscribed to" +
            "\nsub {topic_id}: subscribe to the topic with the given id" +
            "\ncurrent: show all topics to which you are currently subscribed" +
            "\nunsub {topic_id}: unsubscribe from the given topic" +
            "\nd/disconnect: disconnect from the network (warning: all current subscriptions will be lost!)";
    public static void main(String[] args) {
        int port = new PortVerifier().verifyPort(args, 1, 3, USAGE_MESSAGE);
        Subscriber sub = new Subscriber(args[0], args[1], port);
        System.out.println("Welcome, " + args[0] + "." + " (ip: " + args[1] + ", port: " + port + ")");
        System.out.println("Available commands:");
        System.out.println(COMMAND_LIST);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String[] input = scanner.nextLine().split(" ");
            if (!handleInput(sub, input)) {
                return;
            }
        }
    }
    private static boolean handleInput(Subscriber sub, String[] input) {
        switch (input[0]) {
            case "h":
            case "help":
                System.out.println(COMMAND_LIST);
                break;
            // close connection
            case "disconnect":
            case "d":
                System.out.println("Logging out");
                return false;
            case "list":
                sub.listAllAvailableTopics();
                break;
            case "sub":
                try {
                    int id = verifyTopicId(input, 1, 2);
                    sub.subscribeToTopic(id);
                }
                catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
                break;
            case "current":
                sub.showCurrentSubscriptions();
                break;
            case "unsub":
                try {
                    int id = verifyTopicId(input, 1, 2);
                    sub.unsubscribe(id);
                }
                catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
                break;
            default:
                System.out.println("Unrecognised command. Press \"h\" for a list of commands" + ".");
        }
        return true;
    }
    private static int verifyTopicId(String input[], int index, int numArguments) {
        String usage = "Usage: sub {topic_id}";
        if (input.length != numArguments) {
            throw new IllegalArgumentException(usage);
        }
        try {
            return Integer.parseInt(input[1]);
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException(usage);
        }
    }
}
