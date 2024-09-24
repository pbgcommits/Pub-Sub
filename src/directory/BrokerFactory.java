package directory;

import broker.Broker;
import publisher.Publisher;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class BrokerFactory extends UnicastRemoteObject {
    Registry registry;
    protected BrokerFactory(Registry registry) throws RemoteException {
        this.registry = registry;
    }

    public Broker createSubscriber() {
        return new Broker();
    }
}
