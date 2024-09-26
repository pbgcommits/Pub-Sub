package Shared;

import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IDirectory extends Remote {


    void addBroker(String id) throws RemoteException;

    void addPublisher(String username) throws AlreadyBoundException, RemoteException;

    void addSubscriber(String username) throws AlreadyBoundException, RemoteException;
}
