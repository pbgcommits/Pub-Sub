package Server;

import Shared.IPublisherFactory;
import broker.Publisher;

import java.rmi.AlreadyBoundException;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

@Deprecated
public class PublisherFactory extends UnicastRemoteObject implements IPublisherFactory {
    Registry registry;
    protected PublisherFactory(Registry registry) throws RemoteException {
        this.registry = registry;
    }

    @Override
    public void createPublisher(String username) throws AlreadyBoundException, RemoteException {
//        registry.bind(username, new Publisher(username));
    }
}
