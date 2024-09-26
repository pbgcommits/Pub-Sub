package Shared;

import broker.Broker;

import java.rmi.RemoteException;
@Deprecated
public interface IBrokerFactory {

    Broker createBroker(int id) throws RemoteException;
}
