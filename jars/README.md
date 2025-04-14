How to run the program:

1. Run the directory using (this will start the rmi registry on IP localhost) the command:
java -jar directory.jar {rmi_port}

2. Run as many brokers as desired (make sure each has a different broker_port) using command:
java -jar broker.jar {broker_ip} {broker_port} {rmi_ip} {rmi_port}

3. Run as many publishers as desired (make sure each has a different username):
java -jar publisher.jar {username} {rmi_ip} {rmi_port}

4. Run as many subscribers as desired (make sure each has a different username):
java -jar subscriber.jar {username} {rmi_ip} {rmi_port}