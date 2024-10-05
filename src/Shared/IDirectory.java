package Shared;

import java.net.Socket;
import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.NoSuchElementException;

public interface IDirectory extends Remote {

    void addBroker(String id) throws RemoteException;

    //    // TODO: I'm going to have to make sure this is all synchronized - might be a use for semaphores?? idk
    //    // alternatively just
    //    public void connectToBroker() throws NoSuchElementException {
    //        IBroker b = getMostAvailableBroker();
    //        // TODO: do other stuff
    //    }
    IBroker getMostAvailableBroker() throws NoSuchElementException, RemoteException;

//    void addPublisher(Socket client, String username) throws AlreadyBoundException, RemoteException;

//    void addSubscriber(Socket client, String username) throws AlreadyBoundException, RemoteException;
}
