# Publisher-Subscriber network

This project implements a publisher-subscriber network in Java. It makes use of TCP sockets, as well as Java's RMI.

A number of brokers exist, which publishers and subscribers can register with. Publishers can create and publish to topics; subscribers can subscribe to topics, which they will then recieve messages from.

The use of brokers creates a system which is time- and space-decoupled.
