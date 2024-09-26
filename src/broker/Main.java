package broker;

import Shared.IBroker;
import Shared.IDirectory;
import Shared.InputVerifier;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main {
    final static String USAGE_MESSAGE = "java -jar broker.jar {broker_ip} {broker_port} {directory_ip} {directory_port}";
    final static InputVerifier v = new InputVerifier();
    public static void main(String[] args) {
        int brokerPort;
        int directoryPort;
        try {
            brokerPort = v.verifyPort(args, 1, 4, USAGE_MESSAGE);
            directoryPort = v.verifyPort(args, 3, 4, USAGE_MESSAGE);
        }
        catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        }
        String brokerIP = args[0];
        String directoryIP = args[2];
        Broker broker;
        try {
            Registry directoryRegistry = LocateRegistry.getRegistry(directoryIP, directoryPort);
            Registry brokerRegistry = LocateRegistry.getRegistry(brokerIP, brokerPort);
            IDirectory directory = (IDirectory) directoryRegistry.lookup("Directory");
            broker = new Broker(brokerIP, brokerPort, brokerRegistry);
            directoryRegistry.bind(broker.getId(), broker);
            directory.addBroker(broker.getId());
//            broker = (IBroker) registry.lookup("Broker" + brokerPort);
        }
        catch (AlreadyBoundException | RemoteException | NotBoundException e) {
            System.out.println(e.getMessage());
            return;
        }
    }
}
