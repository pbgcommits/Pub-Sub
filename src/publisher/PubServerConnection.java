package publisher;

import Shared.Messenger;
import Shared.Timeouts;
import broker.Publisher;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;


public class PubServerConnection extends Thread {
    private Socket s;
    public PubServerConnection(Socket s) {
        this.s = s;
    }

    @Override
    public void run() {
        String online = new Messenger().writeOnlineMessage();
        try {
            s.setSoTimeout(Timeouts.CLIENT_TIMEOUT);
        }
        catch (SocketException e) {
            System.out.println("Connection failed; please try again later");
            System.exit(1);
        }
        try {
            while (true) {
                try {
                    DataOutputStream output = new DataOutputStream(s.getOutputStream());
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

