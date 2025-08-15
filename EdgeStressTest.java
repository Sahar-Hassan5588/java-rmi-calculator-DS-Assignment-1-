import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.UUID;

/*
  EdgeStressTest performs comprehensive edge-case and stress testing
  of the Calculator RMI service, including concurrency, large values,
  delayPop, and exception handling.
 */
public class EdgeStressTest {

    public static void main(String[] args) {
        try {
            Calculator calc = (Calculator) Naming.lookup("rmi://localhost/Calculator");
            String clientId = "EdgeStressTest-" + UUID.randomUUID();
            System.out.println(" Edge Case & Stress Test");
            System.out.println("Client ID: " + clientId + "\n");

            // 1. Pop from empty stack
            System.out.println("1. Pop from empty stack");
            try {
                calc.pop(clientId);
                System.out.println("   Result: FAIL (expected exception)");
            } catch (RemoteException e) {
                System.out.println("   Result: PASS (caught expected exception)");
            }
            System.out.println();

            // 2. Push & pop extreme integer values
            System.out.println("2. Push & pop extreme integer values");
            int maxInt = Integer.MAX_VALUE;
            int minInt = Integer.MIN_VALUE;
            calc.pushValue(clientId, maxInt);
            calc.pushValue(clientId, minInt);

            int pop1 = calc.pop(clientId);
            int pop2 = calc.pop(clientId);
            System.out.println("   Popped minInt: " + (pop1 == minInt ? "PASS" : "FAIL") + " (value=" + pop1 + ")");
            System.out.println("   Popped maxInt: " + (pop2 == maxInt ? "PASS" : "FAIL") + " (value=" + pop2 + ")");
            System.out.println();

            // 3. GCD / LCM with zero / negative numbers
            System.out.println("3. GCD / LCM edge cases");
            calc.pushValue(clientId, 0);
            calc.pushValue(clientId, 42);
            calc.pushOperation(clientId, "gcd");
            int gcdResult = calc.pop(clientId);
            System.out.println("   GCD with zero: " + (gcdResult == 42 ? "PASS" : "FAIL") + " (value=" + gcdResult + ")");

            calc.pushValue(clientId, -12);
            calc.pushValue(clientId, 20);
            calc.pushOperation(clientId, "lcm");
            int lcmResult = calc.pop(clientId);
            System.out.println("   LCM with negative: " + (lcmResult == 60 ? "PASS" : "FAIL") + " (value=" + lcmResult + ")");
            System.out.println();

            // 4. Min / Max with single-element stack
            System.out.println("4. Min / Max with single-element stack");
            calc.pushValue(clientId, 10);
            calc.pushOperation(clientId, "min");
            int minResult = calc.pop(clientId);
            System.out.println("   Min with one value: " + (minResult == 10 ? "PASS" : "FAIL") + " (value=" + minResult + ")");
            System.out.println();

            // 5. delayPop test
            System.out.println("5. delayPop test (2s)");
            calc.pushValue(clientId, 99);
            long start = System.currentTimeMillis();
            int delayedPop = calc.delayPop(clientId, 2000);
            long elapsed = System.currentTimeMillis() - start;
            System.out.println("   delayPop: " + (delayedPop == 99 && elapsed >= 1900 ? "PASS" : "FAIL") +
                               " (value=" + delayedPop + ", elapsed=" + elapsed + "ms)");
            System.out.println();

            // 6. Multi-threaded concurrency test
            System.out.println("6. Multi-threaded concurrency test");
            Thread[] threads = new Thread[5];
            for (int i = 0; i < threads.length; i++) {
                int t = i;
                threads[i] = new Thread(() -> {
                    String threadId = clientId + "-T" + t;
                    try {
                        for (int j = 0; j < 100; j++) {
                            calc.pushValue(threadId, j);
                        }
                        calc.pushOperation(threadId, "max");
                        int max = calc.pop(threadId);
                        System.out.println("   " + threadId + ": " + (max == 99 ? "PASS" : "FAIL") + " (max=" + max + ")");
                    } catch (RemoteException e) {
                        System.out.println("   " + threadId + ": FAIL (exception " + e.getMessage() + ")");
                    }
                });
                threads[i].start();
            }
            for (Thread t : threads) t.join();
            System.out.println("   Multi-threaded test completed.\n");

            // 7. Rapid stress push/pop
            System.out.println("7. Rapid push/pop stress test (1000 iterations)");
            for (int i = 0; i < 1000; i++) {
                calc.pushValue(clientId, i);
                calc.pop(clientId);
            }
            System.out.println("Stress test completed.\n");

        } catch (Exception e) {
            System.err.println("Stress Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
