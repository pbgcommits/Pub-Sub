package broker.connections;

import shared.util.Messenger;
import shared.util.Timeouts;
import broker.Broker;
import broker.Publisher;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * Maintains a connection with a publisher client. If the publisher disconnects,
 * it will notify all relevant members of the network.
 * @author Patrick Barton Grace 1557198
 */
public class PublisherConnection extends Thread {
    private final Broker broker;
    private final Socket client;
    private final Publisher publisher;
    public PublisherConnection(Socket s, Broker b, Publisher pub) {
        client = s;
        broker = b;
        publisher = pub;
    }
    @Override
    public void run() {
        String online = new Messenger().writeOnlineMessage();
        try {
            client.setSoTimeout(Timeouts.SERVER_TIMEOUT);
        }
        catch (SocketException e) {
            System.out.println("Connection failed; publisher disconnected");
            broker.removePublisher(publisher);
            return;
        }
        try {
            while (true) {
                try {
                    DataInputStream input = new DataInputStream(client.getInputStream());
                    if (!input.readUTF().equals(online)) throw new IOException("publisher has disconnected");
                }
                catch (SocketTimeoutException e) {
                    System.out.println("didn't connect in time");
                }
            }
        }
        catch (IOException e) {
            System.out.println(publisher.getName() + " has disconnected.");
            broker.removePublisher(publisher);
            return;
        }
    }

}
