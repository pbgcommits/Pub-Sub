package directory;

import Shared.Messenger;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.rmi.AlreadyBoundException;
import java.util.NoSuchElementException;

public class Connector extends Thread {
    private Socket s;
    private Directory directory;
    public Connector(Socket s, Directory d) {
        this.s = s;
        this.directory = d;
    }
    @Override
    public void run() {
        try {
            DataInputStream d = new DataInputStream(s.getInputStream());
            String input = d.readUTF();
            Messenger m = new Messenger();
            String connectionType = m.getConnectionType(input);
            String username = m.getUsername(input);
            if (connectionType.equals(m.subscriber())) {
                directory.addSubscriber(s, username);
            } else if (connectionType.equals(m.publisher())) {
                directory.addPublisher(s, username);
            }
        }
        catch (NoSuchElementException | IOException | AlreadyBoundException e) {
            System.out.println("Error connecting new client to broker network");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
