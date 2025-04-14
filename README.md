# Publisher-Subscriber network

This project implements a publisher-subscriber network in Java. It makes use of TCP sockets, as well as Java's RMI.

A number of brokers exist, which publishers and subscribers can register with. Publishers can create and publish to topics; subscribers can subscribe to topics, which they will then recieve messages from.

The use of brokers creates a system which is time- and space-decoupled.

## Features
- Scalable design using the broker pattern - connect initially using a given address, then spread 
 the connections across as many broker servers as required.
- Subscriptions - subscribers subscribe to a given topic published by a publisher to stay up to date with all messages from that publisher. You can also unsubscribe at any time, and see all topics currently available.
- Topics - publishers can create or delete topics at any time. They can publish messages to the topics at any time, and see how many users are currently subscribed to each of their topics.

## How to use

Download this repository. 

1. Open two shells at the ```jars``` repository.
2. In the first one execute
```
java -jar directory.jar {registry_port}
````
 Where {registry_port} is any port number of your choosing.
 This will start the RMI registry.
3. In the second shell, execute
```
java -jar broker.jar localhost {broker_port} localhost {registry_port}
```
Once again, you may pick any port for the broker, so long as it isn't shared with the registry.
You may repeat step 3 as many times as you like to have as many brokers as you like open. 
4. Open a new shell and execute 
```
java -jar publisher.jar {username} localhost {registry_port}
```
Where {username} is a username of your choice for the publisher.
5. Open a new shell and execute 
```
java -jar subscriber.jar {username} localhost {registry_port}
```
Where {username} is a username of your choice for the subscriber.
Repeat steps 4 and 5 as much as you like to have as many publishers and subscribers as you like.
6. You're in!
