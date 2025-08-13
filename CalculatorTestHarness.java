import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class CalculatorTestHarness {

    private static Calculator calc;

    /*
      Main entry point for the CalculatorTestHarness.
      Purpose: Connect to the remote Calculator RMI service, then run a series of automated tests
               to verify core functionalities (pushValue/pushOperation/pop, delayPop timing, and isEmpty state).
      Inputs:  String[] args - not used in this test harness.
      Outputs: Console log indicating PASS/FAIL for each test case and overall completion message.
      Special Cases:
       - If the RMI registry is not running or "Calculator" is not bound, the harness will throw
         and print an exception without executing tests.
       - All tests assume isolated client IDs to avoid data collisions.
     */
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            calc = (Calculator) registry.lookup("Calculator");

            System.out.println("Calculator Test Harness:");
            testPushPopGcd();
            testDelayPop();
            testIsEmpty();
            System.out.println(" All tests finished.");

        } catch (Exception e) {
            System.err.println("Test harness error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /*
      Test pushValue, pushOperation("gcd"), and pop methods for correctness.
      Purpose: Verify that pushing multiple integers and applying a GCD operation
               returns the expected greatest common divisor.
      Inputs:  No direct method arguments, but uses a dedicated clientId ("TestClient-Gcd").
      Outputs: Prints PASS if GCD matches expected value, otherwise FAIL.
      Special Cases:
       - Assumes the server's GCD operation works for positive integers.
       - Assumes stack starts empty for the given clientId.
     */
    private static void testPushPopGcd() throws Exception {
        String clientId = "TestClient-Gcd"+ System.currentTimeMillis();;

        // Push values
        calc.pushValue(clientId, 6);
        calc.pushValue(clientId, 12);
        calc.pushValue(clientId, 24);

        // Perform gcd operation
        calc.pushOperation(clientId, "gcd");

        int result = calc.pop(clientId);
        boolean pass = (result == 6);

        System.out.println("[Test: pushValue, pushOperation(gcd), pop] \n Expected: 6, Got: " + result + " => " + (pass ? "PASS" : "FAIL"));
    }

    /*
      Test delayPop method for both value correctness and timing accuracy.
      Purpose: Ensure delayPop waits approximately the specified number of milliseconds before returning the top value.
      Inputs:  No direct method arguments, but uses clientId ("TestClient-Delay") and a fixed delay of 2000ms.
      Outputs: PASS if returned value is correct AND delay is within acceptable tolerance (±100ms).
      Special Cases:
       - The timing check allows a small tolerance (1900–2100 ms) to account for scheduling delays.
       - Assumes stack starts empty for the given clientId before pushing test value.
     */
    private static void testDelayPop() throws Exception {
        String clientId = "TestClient-Delay"+ System.currentTimeMillis();;

        calc.pushValue(clientId, 7);

        long startTime = System.currentTimeMillis();
        int result = calc.delayPop(clientId, 2000); // expect 2 seconds
        long elapsed = System.currentTimeMillis() - startTime;

        boolean timeOk = elapsed >= 1900 && elapsed <= 2100;
        boolean valueOk = (result == 7);

        System.out.println("[Test: delayPop]\n Expected value= 7, delay≈2000ms | Got value=" + result + ", elapsed=" + elapsed + "ms => " + (timeOk && valueOk ? "PASS" : "FAIL"));
    }

    /*
      Test isEmpty method before and after push/pop operations.
      Purpose: Verify that isEmpty correctly reflects the state of the stack for a given clientId.
      Inputs:  No direct method arguments, but uses clientId ("TestClient-Empty").
      Outputs: PASS if:
               - isEmpty() returns true at the start,
               - isEmpty() returns false after pushing a value,
               - isEmpty() returns true again after popping that value.
      Special Cases:
       - Assumes stack starts empty for the given clientId.
       - Any pre-existing values for the same clientId could cause false FAIL results.
     */
    private static void testIsEmpty() throws Exception {
        String clientId = "TestClient-Empty"+ System.currentTimeMillis();;

        boolean emptyAtStart = calc.isEmpty(clientId);
        calc.pushValue(clientId, 99);
        boolean emptyAfterPush = calc.isEmpty(clientId);
        calc.pop(clientId);
        boolean emptyAfterPop = calc.isEmpty(clientId);

        boolean pass = emptyAtStart && !emptyAfterPush && emptyAfterPop;

        System.out.println("[Test: isEmpty]\n StartEmpty=" + emptyAtStart +
                           ", AfterPush=" + emptyAfterPush +
                           ", AfterPop=" + emptyAfterPop +
                           " => " + (pass ? "PASS" : "FAIL"));
    }
}
