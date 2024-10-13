package broker.connections;

import shared.util.Messenger;
import shared.util.Timeouts;
import broker.Broker;
import broker.Subscriber;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * Maintains a connection with a subscriber client. If the subscriber disconnects, it will notify
 * all relevant members of the network.
 * @author Patrick Barton Grace 1557198
 */
public class SubscriberConnection extends Thread {
    private final Broker broker;
    private final Socket client;
    private final Subscriber subscriber;
    public SubscriberConnection(Socket s, Broker b, Subscriber sub) {
        client = s;
        broker = b;
        subscriber = sub;
    }
    @Override
    public void run() {
        String online = new Messenger().writeOnlineMessage();
        try {
            client.setSoTimeout(Timeouts.SERVER_TIMEOUT);
        }
        catch (SocketException e) {
            System.out.println("Socket connection failed; subscriber disconnected");
            broker.removeSubscriber(subscriber);
            e.printStackTrace();
            return;
        }
        try {
            while (true) {
                try {
                    DataInputStream input = new DataInputStream(client.getInputStream());
                    String in = input.readUTF();
                    if (!in.equals(online)) throw new IOException();
                    if (subscriber.hasMessage()) {
                        DataOutputStream output = new DataOutputStream(client.getOutputStream());
                        output.writeUTF(subscriber.getMessage());
                        output.flush();
                    }
                }
                catch (SocketTimeoutException e) {
                    System.out.println("Didn't connect in time!!!!");
                }
            }
        }
        catch (IOException e) {
            System.out.println(subscriber.getName() + " has disconnected");
            broker.removeSubscriber(subscriber);
            return;
        }
    }
}
