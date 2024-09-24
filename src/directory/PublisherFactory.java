package directory;

import Shared.IPublisherFactory;
import publisher.Publisher;

import java.rmi.AlreadyBoundException;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class PublisherFactory extends UnicastRemoteObject implements IPublisherFactory {
    Registry registry;
    protected PublisherFactory(Registry registry) throws RemoteException {
        this.registry = registry;
    }

    @Override
    public boolean createPublisher(String username) throws RemoteException {
        try {
            registry.bind(username, new Publisher(username));
        }
        catch (AlreadyBoundException e) {
            return false;
        }
        return true;
    }
}
