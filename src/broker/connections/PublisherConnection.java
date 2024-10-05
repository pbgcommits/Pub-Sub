package broker.connections;

import Shared.Messenger;
import Shared.Timeouts;
import broker.Broker;
import broker.Publisher;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

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
//    @Override
//    public void run() {
//        String online = new Messenger().writeOnlineMessage();
//        try {
//            client.setSoTimeout(Timeouts.SERVER_TIMEOUT);
////            client.setKeepAlive(true);
//        }
//        catch (SocketException e) {
//            broker.removePublisher(publisher);
//            System.out.println("well that went poorly for keeping it alive");
//        }
//        try {
//            while (true) {
//                System.out.println("checking " + publisher.getName() + " connection");
//                try {
////                Thread.sleep(1000);
//                    DataInputStream input = new DataInputStream(client.getInputStream());
//                    if (!input.readUTF().equals(online)) throw new IOException("custom message");
//                }
//                catch (SocketTimeoutException e) {
//                    System.out.println("didn't connect in time!!!");
//                }
////            catch (InterruptedException e) {
////                System.out.println("weird! interrupted exception");
////                return;
////            }
//            }
//        }
//        catch (IOException e) {
//            System.out.println("Client disconnected");
//            System.out.println(e.getMessage());
//            broker.removePublisher(publisher);
//            return;
//        }
//    }
}
