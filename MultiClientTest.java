import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MultiClientTest {

    /*
      ClientThread simulates a client interacting with the Calculator RMI service.
      Each thread uses a unique clientId string based on the integer id provided.

      Inputs:
        - id: integer used to create a unique clientId string (such as "Client-1").
     
      Steps:
        - Connects to the Calculator RMI service.
        - Pushes a distinct set of integer values multiplied by 10 plus an offset to avoid data collision.
        - Checks if the client's stack is initially not empty after pushing values.
        - Performs a "min" operation on the stack as test for the supported operations.
        - Pops and verifies the minimum value.
        - Checks isEmpty after pop to verify stack emptiness.
        - Pushes two additional values and tests delayPop for both value correctness and delay duration.
      
      Outputs:
        - Prints PASS/FAIL messages to standard output based on each verification step.
      
      Special Cases:
        - Assumes that each clientId has an isolated stack on the server side.
        - Expects delayPop to delay approximately 2000ms before returning the top stack value.
     */
    static class ClientThread extends Thread {
        private final String clientId;

        ClientThread(int id) {
            // Create a unique clientId string based on the int id
            this.clientId = "Client-" + id;
        }

        /*
          Run method performs all client interactions:
            - Registry lookup of Calculator.
            - Push, operation, pop, and isEmpty.
            - Delay pop timing test.
          Throws exceptions will be caught and logged.
         */
        @Override
        public void run() {
            try {
                Registry registry = LocateRegistry.getRegistry("localhost", 1099);
                Calculator calc = (Calculator) registry.lookup("Calculator");

                // Each client pushes some values with clientId; 
                // multiply by 10 to ensure each client’s values are in a distinct range to avoid collisions.
                calc.pushValue(clientId, Integer.parseInt(clientId.replace("Client-", "")) * 10 + 1);
                calc.pushValue(clientId, Integer.parseInt(clientId.replace("Client-", "")) * 10 + 2);
                calc.pushValue(clientId, Integer.parseInt(clientId.replace("Client-", "")) * 10 + 3);
                calc.pushValue(clientId, Integer.parseInt(clientId.replace("Client-", "")) * 10 + 4);
                 
                // Verify stack is not empty before operations
                if (!calc.isEmpty(clientId)) {
                    System.out.println(clientId + " initial isEmpty check: PASS (stack not empty)");
                } else {
                    System.out.println(clientId + " initial isEmpty check: FAIL (stack unexpectedly empty)");
                }

                // Each client does a min operation
                calc.pushOperation(clientId, "min");

                // Each client pops the result
                int result = calc.pop(clientId);
                System.out.println(clientId + " popped: " + result);

                // Simple check: min of values pushed
                int clientNum = Integer.parseInt(clientId.replace("Client-", ""));
                int expectedMin = clientNum * 10 + 1;
                if (result == expectedMin) {
                    System.out.println(clientId + " test: PASS");
                } else {
                    System.out.println(clientId + " test: FAIL (expected " + expectedMin + ")");
                }


                // Test isEmpty - should be true after popping min result
                boolean emptyAfter = calc.isEmpty(clientId);
                System.out.println(clientId + " isEmpty after pop: " + emptyAfter);
                if (emptyAfter) {
                    System.out.println(clientId + " isEmpty test: PASS");
                } else {
                    System.out.println(clientId + " isEmpty test: FAIL (stack not empty)");
                }

                // Push some values again to test delayPop
                calc.pushValue(clientId, clientNum * 10 + 5);
                calc.pushValue(clientId, clientNum * 10 + 6);

                long startTime = System.currentTimeMillis();
                int delayedPopValue = calc.delayPop(clientId, 2000); // 2 second delay
                long endTime = System.currentTimeMillis();
                long elapsed = endTime - startTime;

                int expectedDelayed = clientNum * 10 + 6; // Last pushed value
                System.out.println(clientId + " delayPop elapsed: " + elapsed + " ms");
                if (delayedPopValue == expectedDelayed && elapsed >= 2000) {
                    System.out.println(clientId + " delayPop test: PASS");
                } else {
                    System.out.println(clientId + " delayPop test: FAIL (value or delay incorrect)");
                }

                
            } catch (Exception e) {
                System.err.println(clientId + " error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /*
       Main method launches multiple ClientThread instances in parallel.
       Purpose: Test the Calculator service with multiple concurrent clients using distinct clientIds.
       Inputs: None
       Outputs: Prints test progress and final completion message.
       Special Cases:
        - Threads are joined to ensure main waits for all clients to finish.
        - Assumes the Calculator RMI service is available on localhost port 1099.
     */
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Multi-Client Testing:");

        ClientThread client1 = new ClientThread(1);
        ClientThread client2 = new ClientThread(2);
        ClientThread client3 = new ClientThread(3);
        ClientThread client4 = new ClientThread(4);

        client1.start();
        client2.start();
        client3.start();
        client4.start();

        client1.join();
        client2.join();
        client3.join();
        client4.join();

        System.out.println("Multi-client test finished.");
    }
}
