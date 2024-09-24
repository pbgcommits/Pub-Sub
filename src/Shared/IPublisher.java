package Shared;

import javax.naming.LimitExceededException;
import java.util.NoSuchElementException;

public interface IPublisher {

    int createNewTopic(String name) throws LimitExceededException;

    void publish(int id, String message) throws NoSuchElementException;

    int show(int id) throws NoSuchElementException;

    void delete(int id) throws NoSuchElementException;
}
