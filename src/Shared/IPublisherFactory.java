package Shared;

import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IPublisherFactory extends Remote {

    void createPublisher(String username) throws AlreadyBoundException, RemoteException;
}
