# java-rmi-calculator-DS-Assignment-1-
## Overview
This project implements a distributed calculator system using Java Remote Method Invocation (RMI).
The calculator server maintains a shared stack that clients can interact with remotely to perform operations such as pushing values, applying mathematical operations (min, max, gcd, lcm), popping values, checking if the stack is empty, and delayed popping.
**The objective of this assignment**  is to demonstrate understanding of remote method invocation, synchronization, and multi-client distributed systems in Java.

## Files Included
+ **Calculator.java:** The remote interface defining the methods accessible by clients.
+ **CalculatorImplementation.java:** The server-side implementation of the Calculator interface.
+ **CalculatorServer.java:** The server bootstrap class that starts the RMI registry and binds the service.
+ **CalculatorClient.java:** A client application to test the remote methods.
+ **AutomatedTestClient.java:** Additional client to automate testing with single and multiple clients.

## How to Compile and Run the Java RMI Calculator Application
1. Open your Linux terminal.
2. Navigate to the directory containing all Java source files.
3. Compile all Java source files using the following command:
```
javac *.java
```

This command generates the necessary .class files for server and client execution.
5. Start the Calculator Server by Run the server class:
```
java CalculatorServer
```
**Note:** The RMI registry is started automatically within this server using LocateRegistry.createRegistry(1099).
You do not need to manually start the rmiregistry process from the terminal.

You should see a message:
```
Calculator server is running...
```
6. Run the Client to Test Remote Methods by opening new terminal window (same directory), run the client:
```
java CalculatorClient
```
This will connect to the server and test all the remote methods like pushValue, pushOperation, pop, delayPop, and isEmpty.
The client output will show the results of each operation.

## Environment
These commands assume a Linux or Mac terminal environment with Java JDK installed and java & javac in your PATH.





