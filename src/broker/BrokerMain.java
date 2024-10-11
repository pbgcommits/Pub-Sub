package broker;

import broker.*;
import shared.IDirectory;
import shared.InputVerifier;
import broker.connections.BrokerConnection;

import javax.net.ServerSocketFactory;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class BrokerMain {
    private final static String USAGE_MESSAGE = "java -jar broker.jar " +
            "{broker_ip} {broker_port} {registry_ip} {registry_port}";
    private final static int NUM_ARGS = 4;
    private final static InputVerifier v = new InputVerifier();
    private static Broker broker;
    public static void main(String[] args) {
        int brokerPort;
        int rmiPort;
        try {
            brokerPort = v.verifyPort(args, 1, NUM_ARGS, USAGE_MESSAGE);
            rmiPort = v.verifyPort(args, 3, NUM_ARGS, USAGE_MESSAGE);
        }
        catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        }
        String brokerIP = args[0];
        String rmiIP = args[2];
        try {
            Registry directoryRegistry = LocateRegistry.getRegistry(rmiIP, rmiPort);
            IDirectory directory = (IDirectory) directoryRegistry.lookup("Directory");
            broker = new Broker(brokerIP, brokerPort, directoryRegistry);
//            System.out.println('1');
            try {
                directoryRegistry.bind(broker.getId(), broker);
            }
            catch (AlreadyBoundException e) {
                directoryRegistry.rebind(broker.getId(), broker);
            }
//            System.out.println('2');
            directory.addBroker(broker.getId());
//            System.out.println('3');
//            broker = (IBroker) registry.lookup("Broker" + brokerPort);
        }
        catch (RemoteException | NotBoundException e) {
            System.out.println("Directory is currently not online; please try again later.");
            e.printStackTrace();
            System.exit(1);
            return;
        }
        System.out.println("Broker is now connected to the network");
        try (ServerSocket server = ServerSocketFactory.getDefault().createServerSocket(brokerPort)) {
            while (true) {
                Socket client = server.accept();
                new BrokerConnection(client, broker).start();
                // deal with client
//                break;
            }
        }
        catch (IOException e) {
            System.out.println("Socket exception");
        }
    }
}
