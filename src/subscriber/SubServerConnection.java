package subscriber;

import shared.util.Messenger;
import shared.util.Timeouts;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Date;

/**
 * Maintains a connection between a subscriber and the server.
 * @author Patrick Barton Grace 1557198
 */
public class SubServerConnection extends Thread {
    private final Socket server;
    public SubServerConnection(Socket server) {
        this.server = server;
    }
    @Override
    public void run() {
        String online = new Messenger().writeOnlineMessage();
        try {
            // Thanks to https://stackoverflow.com/questions/29685705/permanent-and-persistent-socket-connection-in-java
            // for this
            server.setSoTimeout(Timeouts.CLIENT_TIMEOUT);
        }
        catch (SocketException e) {
            System.out.println("Connection failed; please try again later");
            e.printStackTrace();
            System.exit(1);
            return;
        }
        try {
            while (true) {
                try {
                    DataOutputStream output = new DataOutputStream(server.getOutputStream());
                    output.writeUTF(online);
                    output.flush();
                    DataInputStream input = new DataInputStream(server.getInputStream());
                    Date date = new Date();
                    System.out.println(date.toString().substring(0, 19) + " % " + input.readUTF());
                }
                catch (SocketTimeoutException e) {
                }
            }
        }
        catch (IOException e) {
            System.out.println("Server has gone offline!");
            return;
        }
        finally {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                    System.out.println("socket didn't close!!");
                    throw new RuntimeException(e);
                }
            }
            System.exit(1);
        }
    }
}
