import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class SingleClientTest {
    /*
      Main method runs 5 tests of (push/pop/pushOperation/isEmpty and delayPop) methods on Calculator RMI service
      for a single client identified by a unique clientId.
      
      Inputs:
        - None from args, connects to localhost RMI registry at port 1099
      
      Outputs:
        - Console output of pass/fail for each test
      
      Special cases:
        - Clears stack initially by popping all values if any exist
        - Measures approximate delay in delayPop test
     */
    public static void main(String[] args) {
        try {
            // Connect to RMI registry and lookup Calculator service
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            Calculator calc = (Calculator) registry.lookup("Calculator");

            // // Unique clientId ensures single/multiple SingleClientTest instances don’t overlap
            String clientId = "SingleClientTest"+ System.currentTimeMillis(); 

            System.out.println("Single Client Testing with Pass/Fail Checks");

            int passed = 0, failed = 0;

            // Clear stack first (pop all if any)
            while (!calc.isEmpty(clientId)) {
                calc.pop(clientId);
            }

            // Test 1: pushValue and pop correctness
            try {
                calc.pushValue(clientId, 10);
                calc.pushValue(clientId, 5);
                int val1 = calc.pop(clientId);
                int val2 = calc.pop(clientId);
                if (val1 == 5 && val2 == 10) {
                    System.out.println("[Test 1] pushValue/pop: PASS");
                    passed++;
                } else {
                    System.out.println("[Test 1] pushValue/pop: FAIL - popped " + val1 + ", " + val2);
                    failed++;
                }
            } catch (Exception e) {
                System.out.println("[Test 1] pushValue/pop: FAIL - Exception: " + e.getMessage());
                failed++;
            }

            // Test 2: pushOperation with max
            try {
                calc.pushValue(clientId, 5);
                calc.pushValue(clientId, 10);
                calc.pushOperation(clientId, "max");
                int maxResult = calc.pop(clientId);
                if (maxResult == 10) {
                    System.out.println("[Test 2] pushOperation max: PASS");
                    passed++;
                } else {
                    System.out.println("[Test 2] pushOperation max: FAIL - result " + maxResult);
                    failed++;
                }
            } catch (Exception e) {
                System.out.println("[Test 2] pushOperation max: FAIL - Exception: " + e.getMessage());
                failed++;
            }

            // Test 3: pushOperation with lcm
            try {
                calc.pushValue(clientId, 4);
                calc.pushValue(clientId, 6);
                calc.pushValue(clientId, 8);
                calc.pushOperation(clientId, "lcm");
                int lcmResult = calc.pop(clientId);
                if (lcmResult == 24) {
                    System.out.println("[Test 3] pushOperation lcm: PASS");
                    passed++;
                } else {
                    System.out.println("[Test 3] pushOperation lcm: FAIL - result " + lcmResult);
                    failed++;
                }
            } catch (Exception e) {
                System.out.println("[Test 3] pushOperation lcm: FAIL - Exception: " + e.getMessage());
                failed++;
            }

            // Test 4: isEmpty after popping all
            try {
                boolean empty = calc.isEmpty(clientId);
                if (empty) {
                    System.out.println("[Test 4] isEmpty: PASS");
                    passed++;
                } else {
                    System.out.println("[Test 4] isEmpty: FAIL - stack not empty");
                    failed++;
                }
            } catch (Exception e) {
                System.out.println("[Test 4] isEmpty: FAIL - Exception: " + e.getMessage());
                failed++;
            }

            // Test 5: delayPop with delay approx 2 seconds
            try {
                calc.pushValue(clientId, 30);
                long start = System.currentTimeMillis();
                int val = calc.delayPop(clientId, 2000);  // 2 seconds delay
                long duration = System.currentTimeMillis() - start;

                if (val == 30 && duration >= 1900) {
                    System.out.println("[Test 5] delayPop: PASS (duration " + duration + " ms)");
                    passed++;
                } else {
                    System.out.println("[Test 5] delayPop: FAIL - val=" + val + ", duration=" + duration + " ms");
                    failed++;
                }
            } catch (Exception e) {
                System.out.println("[Test 5] delayPop: FAIL - Exception: " + e.getMessage());
                failed++;
            }

            // Summary output of all tests passed vs failed
            System.out.println("\nTest Summary:");
            System.out.println("Passed: " + passed);
            System.out.println("Failed: " + failed);

        } catch (Exception e) {
            System.err.println("Client error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
