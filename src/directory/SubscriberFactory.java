package directory;

import Shared.ISubscriberFactory;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class SubscriberFactory extends UnicastRemoteObject implements ISubscriberFactory {

    Registry registry;
    protected SubscriberFactory(Registry registry) throws RemoteException {
        this.registry = registry;
    }

    public boolean createSubscriber(String username) throws RemoteException {
        try {
            registry.bind(username, new Subscriber(username));
        }
        catch (AlreadyBoundException e) {
            return false;
        }
        return true;
    }
}
