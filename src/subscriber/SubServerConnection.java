package subscriber;

import Shared.Messenger;
import Shared.Timeouts;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class SubServerConnection extends Thread {
    private Socket s;
    public SubServerConnection(Socket s) {
        this.s = s;
    }
    @Override
    public void run() {
        String online = new Messenger().writeOnlineMessage();
        try {
            // Thanks to https://stackoverflow.com/questions/29685705/permanent-and-persistent-socket-connection-in-java
            // for this
            s.setSoTimeout(Timeouts.CLIENT_TIMEOUT);
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
                    DataOutputStream output = new DataOutputStream(s.getOutputStream());
                    output.writeUTF(online);
                    output.flush();
                    DataInputStream input = new DataInputStream(s.getInputStream());
                    System.out.println(input.readUTF());
                }
                catch (SocketTimeoutException e) {
//                    System.out.println("timed out!!");
                }
            }
        }
        catch (IOException e) {
            System.out.println("Server has gone offline!");
            return;
        }
        finally {
            if (s != null) {
                try {
                    s.close();
                } catch (IOException e) {
                    System.out.println("socket didn't close!!");
                    throw new RuntimeException(e);
                }
            }
            System.exit(1);
        }
    }
}
