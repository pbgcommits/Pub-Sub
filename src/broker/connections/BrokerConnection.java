package broker.connections;

import shared.util.Messenger;
import broker.Broker;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.NoSuchElementException;

/**
 * Adds new subscribers and publishers to the network.
 * @author Patrick Barton Grace 1557198
 */
public class BrokerConnection extends Thread {
    private Socket socket;
    private Broker broker;
    public BrokerConnection(Socket socket, Broker b) {
        this.socket = socket;
        this.broker = b;
    }
    @Override
    public void run() {
        try {
            DataInputStream d = new DataInputStream(socket.getInputStream());
            String input = d.readUTF();
            Messenger m = new Messenger();
            String username = m.getUsername(input);
            if (m.isSubscriber(input)) {
                System.out.println("Adding subscriber: " + username);
                broker.addSubscriber(socket, username);
            }
            else if (m.isPublisher(input)) {
                System.out.println("Adding publisher: " + username);
                broker.addPublisher(socket, username);
            }
            else {
                System.out.println("Invalid connection request: " + input);
            }
        }
        catch (NoSuchElementException | IOException e) {
            System.out.println("Error connecting new client to broker network");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
