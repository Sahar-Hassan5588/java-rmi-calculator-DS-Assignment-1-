# java-rmi-calculator-DS-Assignment-1-
## Overview
This project implements a distributed calculator system using Java Remote Method Invocation (RMI).
The calculator server maintains a shared stack that clients can interact with remotely to perform operations such as pushing values, applying mathematical operations (min, max, gcd, lcm), popping values, checking if the stack is empty, and delayed popping. <br/>
**The objective of this assignment**  is to demonstrate understanding of remote method invocation, synchronization, and multi-client distributed systems in Java.

## Files Included
+ **Calculator.java:** The remote interface defining the methods accessible by clients.
+ **CalculatorImplementation.java:** The server-side implementation of the Calculator interface.
+ **CalculatorServer.java:** The server bootstrap class that starts the RMI registry and binds the service.
+ **CalculatorClient.java:** A client application to test the remote methods.
+ **AutomatedTestClient.java:** Additional client to automate testing with single and multiple clients.

## How to Compile and Run the Java RMI Calculator Application
1. Open your Linux or Mac terminal.
2. Navigate to the directory containing all Java source files.
3. **Compile all Java source files** using the following command:
```
javac *.java
```

This command generates the necessary `.class` files for server and client execution.<br/><br/>
5. **Start the Calculator Server** by Run the server class:
```
java CalculatorServer
```
**Note:** The RMI registry is started automatically within this server using `LocateRegistry.createRegistry(1099)`.
You do not need to manually start the `rmiregistry` process from the terminal.<br/><br/>

You should see a message:
`
Calculator server is running...
`
<br/><br/> 6. **Run the Client** to Test Remote Methods by opening new terminal window (same directory), run the client:
```
java CalculatorClient
```
This will connect to the server and start an **interactive Calculator Client** where you can manually test all remote methods.<br/>
Upon connection, the client is assigned a unique clientId, and you can select from a menu to: <br/>
1. Push values onto the stack.
2. Perform operations (min, max, gcd, lcm) on the stack contents.
3. Pop values from the stack.
4. Delay and then pop a value after a specified time in milliseconds.
5. Check if the stack is empty.
6. Exit the client. <br/> The program provides prompts and feedback for each action, including skipping invalid inputs, preventing operations on an empty stack, and displaying results of operations.

## Simulating Multiple Clients
To simulate multiple clients concurrently, open additional terminal windows and run the client program `java CalculatorClient` simultaneously in each. This demonstrates multiple clients accessing the same remote server stack.

## Calculator RMI Testing
This project provides automated test clients to verify the functionality and concurrency behavior of the `Calculator RMI` service. Tests cover single-client and multi-client scenarios, including stack operations, delayPop timing, and operation correctness.

**1. SingleClientTest**
- Uses a unique clientId for isolation.
- Clears the stack before starting.
- Performs 5 tests: `pushValue/pop`, `pushOperation(max)`, `pushOperation(lcm)`, `isEmpty`, `delayPop`.
- Outputs PASS/FAIL for each test and summary.
- Can run multiple instances in parallel without conflicts.

**Run:**
```
java SingleClientTest
```

**2. CalculatorTestHarness**
- Structured automated tests for core Calculator functionality.
- Tests `pushValue`, `pop`, `pushOperation(gcd)`, `delayPop`, `isEmpty`.
- Each test uses a unique clientId.
- Logs detailed PASS/FAIL messages.
  
**Run:**
```
java CalculatorTestHarness
```

**3. MultiClientTest**
- Simulates multiple clients concurrently to test stack isolation and thread safety.
- Launches multiple ClientThread instances with unique clientIds.
- Each client performs `pushValue`, `pushOperation(min)`, `pop`, `isEmpty`, `delayPop`.
- Prints PASS/FAIL per client and operation.

**Run:**
```
java MultiClientTest
```

**Notes:**
- RMI registry must run on localhost:1099 with "Calculator" bound.
- Each client uses an isolated stack.
- Don't forget to run the Server first before any test.
  
## Summary of Terminal Commands 
```
# Compile all source files
javac *.java

# Start the server (also starts RMI registry)
java CalculatorServer

# In one or more new terminal windows, start clients
java CalculatorClient

```
## Summary of Testing Commands

```
# Compile all source files
javac *.java

# Start the Calculator server (ensure RMI registry runs)
java CalculatorServer

# Run single-client automated test
java SingleClientTest

# Run structured test harness
java CalculatorTestHarness

# Run multi-client concurrency test
java MultiClientTest
```

## Environment
These commands assume a Linux or Mac terminal environment with Java JDK installed and java & javac in your PATH.





