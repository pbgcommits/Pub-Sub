package broker;

import Shared.Messenger;
import Shared.Timeouts;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

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
//            System.exit(1);
            return;
        }
        try {
            while (true) {
//                System.out.println("checking " + subscriber.getName() + " connection");
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
