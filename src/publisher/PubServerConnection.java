package publisher;

import shared.util.Messenger;
import shared.util.Timeouts;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;


/**
 * Maintain a connection between a publisher and the server.
 * @author Patrick Barton Grace 1557198
 */
public class PubServerConnection extends Thread {
    private final Socket server;
    public PubServerConnection(Socket server) {
        this.server = server;
    }

    @Override
    public void run() {
        String online = new Messenger().writeOnlineMessage();
        try {
            server.setSoTimeout(Timeouts.CLIENT_TIMEOUT);
        }
        catch (SocketException e) {
            System.out.println("Connection failed; please try again later");
            System.exit(1);
        }
        try {
            while (true) {
                try {
                    DataOutputStream output = new DataOutputStream(server.getOutputStream());
                    output.writeUTF(online);
                    output.flush();
                }
                catch (SocketTimeoutException e) {
                    System.out.println("timeout :)");
                }
            }
        }
        catch (IOException e) {
            System.out.println("Server offline :(");
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

//    @Override
//    public void run() {
//        String online = new Messenger().writeOnlineMessage();
//        try {
//            s.setSoTimeout(Timeouts.CLIENT_TIMEOUT);
//        }
//        catch (SocketException e) {
//            System.out.println("Server connection failed");
//            e.printStackTrace();
//            System.exit(1);
//            return;
//        }
//        while (true) {
//            try {
////                Thread.sleep(1000);
//                DataOutputStream output = new DataOutputStream(s.getOutputStream());
//                output.writeUTF(online);
//                output.flush();
//            }
//            catch (SocketTimeoutException e) {
//                System.out.println("Didn't connect in time!!!!");
//            }
//            catch (IOException e) {
//                System.out.println("Server went down :(");
//                return;
//            }
////            catch (InterruptedException e) {
////                System.out.println("Interrupted :(((");
////                return;
////            }
//            finally {
//                try {
//                    s.close();
//                }
//                catch (IOException e) {
//                    System.out.println("socket didn't close!!");
//                    throw new RuntimeException(e);
//                }
//            }
//        }
//    }
}

