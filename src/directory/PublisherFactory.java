package directory;

import Shared.IPublisherFactory;

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
    public void createPublisher(String username) throws AlreadyBoundException, RemoteException {
        registry.bind(username, new Publisher(username));
    }
}
