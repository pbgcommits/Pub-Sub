package shared;

import javax.naming.LimitExceededException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.NoSuchElementException;

public interface IPublisher extends Remote {

    String createNewTopic(String name, String id) throws LimitExceededException, RemoteException;

    void publish(String id, String message) throws NoSuchElementException, RemoteException;

    int show(String id) throws NoSuchElementException, RemoteException;

    void delete(String id) throws NoSuchElementException, RemoteException;
}
