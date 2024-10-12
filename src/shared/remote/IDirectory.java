package shared.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.NoSuchElementException;

/**
 * Remote interface for directory objects.
 * @author Patrick Barton Grace 1557198
 */
public interface IDirectory extends Remote {
    /**
     * Add a new broker to the network.
     * @param id The id of the new broker to add.
     */
    void addBroker(String id) throws RemoteException;
    /**
     * Finds the broker in the network who currently has the least number of connections.
     * @throws NoSuchElementException If there are currently no brokers connected to the network.
     */
    IBroker getMostAvailableBroker() throws NoSuchElementException, RemoteException;

}
