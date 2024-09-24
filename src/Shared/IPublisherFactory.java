package Shared;

import java.rmi.RemoteException;

public interface IPublisherFactory {

    boolean createPublisher(String username) throws RemoteException;
}
