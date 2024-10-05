package broker;

import Shared.Messenger;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.rmi.AlreadyBoundException;
import java.util.NoSuchElementException;

public class BrokerConnector extends Thread {
    private Socket socket;
    private Broker broker;
    public BrokerConnector(Socket socket, Broker b) {
        this.socket = socket;
        this.broker = b;
    }
    @Override
    public void run() {
        try {
            DataInputStream d = new DataInputStream(socket.getInputStream());
            String input = d.readUTF();
            Messenger m = new Messenger();
            String connectionType = m.getConnectionType(input);
            String username = m.getUsername(input);
            if (connectionType.equals(m.subscriber())) {
                System.out.println("Adding subscriber: " + username);
                broker.addSubscriber(socket, username);
            } else if (connectionType.equals(m.publisher())) {
                System.out.println("Adding publisher: " + username);
                broker.addPublisher(socket, username);
            }
        }
        catch (NoSuchElementException | IOException | AlreadyBoundException e) {
            System.out.println("Error connecting new client to broker network");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
