package directory;

public class Main {
    private static Directory directory;
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar directory.jar directory_ip directory_port");
        }
        String ip = args[0];
        String portString = args[1];
        int port;
        try {
            port = Integer.parseInt(portString);
        }
        catch (NumberFormatException e) {
            System.out.println("Usage: java -jar directory.jar directory_ip directory_port");
            return;
        }
        if (0 > port || port > 65535) {
            System.out.println("Port must be between 0 and 65535 (inclusive).");
            return;
        }
        directory = new Directory(ip, port);
        while (true) {
            // TODO: check for incoming brokers, publishers, subscribers, and allocate them accordingly
            break;
        }
    }
}
