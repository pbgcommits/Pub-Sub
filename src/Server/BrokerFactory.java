package Server;

import Shared.IBrokerFactory;
import broker.Broker;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;

@Deprecated
public class BrokerFactory implements IBrokerFactory {
    Registry registry;
    protected BrokerFactory(Registry registry) throws RemoteException {
        this.registry = registry;
    }

    @Override
    public Broker createBroker(int id) throws RemoteException {
        return null;
    }

//    public boolean createBroker(int id) throws RemoteException {
//        try {
//            registry.bind(String.valueOf(id), new Broker(id));
//        } catch (AlreadyBoundException | RemoteException e) {
//            System.out.println(e.getMessage());
//            return false;
//        }
//        return true;
//        return new Broker(id, "MADE UP", 1234);
//    }
}
