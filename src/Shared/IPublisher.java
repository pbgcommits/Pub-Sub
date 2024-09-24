package Shared;

import javax.naming.LimitExceededException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.NoSuchElementException;

public interface IPublisher extends Remote {

    int createNewTopic(String name) throws LimitExceededException, RemoteException;

    void publish(int id, String message) throws NoSuchElementException, RemoteException;

    int show(int id) throws NoSuchElementException, RemoteException;

    void delete(int id) throws NoSuchElementException, RemoteException;
}
