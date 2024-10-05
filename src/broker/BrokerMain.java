package broker;

import Shared.IDirectory;
import Shared.InputVerifier;
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
    private final static InputVerifier v = new InputVerifier();
    private static Broker broker;
    public static void main(String[] args) {
        int brokerPort;
        int rmiPort;
        try {
            brokerPort = v.verifyPort(args, 1, 4, USAGE_MESSAGE);
            rmiPort = v.verifyPort(args, 3, 4, USAGE_MESSAGE);
        }
        catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        }
        String brokerIP = args[0];
        String rmiIP = args[2];
        try {
            Registry directoryRegistry = LocateRegistry.getRegistry(rmiIP, rmiPort);
//            Registry brokerRegistry = LocateRegistry.getRegistry(brokerIP, brokerPort);
            IDirectory directory = (IDirectory) directoryRegistry.lookup("Directory");
            broker = new Broker(brokerIP, brokerPort, directoryRegistry);
            try {
                directoryRegistry.bind(broker.getId(), broker);
            }
            catch (AlreadyBoundException e) {
                directoryRegistry.rebind(broker.getId(), broker);
            }
            directory.addBroker(broker.getId());
//            broker = (IBroker) registry.lookup("Broker" + brokerPort);
        }
        catch (RemoteException | NotBoundException e) {
            System.out.println("one of these");
            System.out.println(e.getMessage());
            e.printStackTrace();
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
