import org.junit.jupiter.api.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CalculatorJUnitTest {

    private Calculator calc;
    
    // Setup method connects to RMI registry and looks up Calculator service.
    @BeforeAll
    public void setup() throws Exception {
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        calc = (Calculator) registry.lookup("Calculator");
    }

    
    // Utility method to generate unique clientId for each test run
    
    private String uniqueClientId(String prefix) {
        return prefix + "-" + System.currentTimeMillis() + "-" + UUID.randomUUID();
    }

    //  SingleClientTest
    @Test
    public void SingleClientTest() throws Exception {
        String clientId = uniqueClientId("SingleClientTest");

        // Clear stack first (pop all if any)
        while (!calc.isEmpty(clientId)) calc.pop(clientId);

        // Test 1: pushValue and pop correctness
        calc.pushValue(clientId, 10);
        calc.pushValue(clientId, 5);
        assertEquals(5, calc.pop(clientId), "Test 1 failed: first pop mismatch");
        assertEquals(10, calc.pop(clientId), "Test 1 failed: second pop mismatch");

        // Test 2: pushOperation with max
        calc.pushValue(clientId, 5);
        calc.pushValue(clientId, 10);
        calc.pushOperation(clientId, "max");
        assertEquals(10, calc.pop(clientId), "Test 2 failed: max operation mismatch");

        // Test 3: pushOperation with lcm
        calc.pushValue(clientId, 4);
        calc.pushValue(clientId, 6);
        calc.pushValue(clientId, 8);
        calc.pushOperation(clientId, "lcm");
        assertEquals(24, calc.pop(clientId), "Test 3 failed: lcm operation mismatch");

        // Test 4: isEmpty after popping all
        assertTrue(calc.isEmpty(clientId), "Test 4 failed: stack should be empty");

        // Test 5: delayPop with delay approx 2 seconds
        calc.pushValue(clientId, 30);
        long start = System.currentTimeMillis();
        int val = calc.delayPop(clientId, 2000);
        long duration = System.currentTimeMillis() - start;
        assertEquals(30, val, "Test 5 failed: delayPop value mismatch");
        assertTrue(duration >= 1900, "Test 5 failed: delayPop duration too short");
    }

    // MultiClientTest
    @Test
    public void MultiClientTest() throws InterruptedException {
        class ClientThread extends Thread {
            private final String clientId;
            ClientThread(int id) { this.clientId = uniqueClientId("Client-" + id); }
            @Override
            public void run() {
                try {
                    // Each client pushes some values with clientId; 
                    calc.pushValue(clientId, 1);
                    calc.pushValue(clientId, 2);
                    calc.pushValue(clientId, 3);
                    calc.pushValue(clientId, 4);
                    // Verify stack is not empty before operations
                    assert !calc.isEmpty(clientId);

                    // Each client does a min operation
                    calc.pushOperation(clientId, "min");
                    // Each client pops the result & check
                    assertEquals(1, calc.pop(clientId));
                    
                    // Test isEmpty - should be true after popping min result
                    assert calc.isEmpty(clientId);

                    // Push some values again to test delayPop
                    calc.pushValue(clientId, 5);
                    calc.pushValue(clientId, 6);
                    long start = System.currentTimeMillis();
                    int val = calc.delayPop(clientId, 2000);
                    long elapsed = System.currentTimeMillis() - start;
                    assertEquals(6, val);
                    assertTrue(elapsed >= 1900);
                } catch (Exception e) {
                    fail("ClientThread exception: " + e.getMessage());
                }
            }
        }

        ClientThread[] threads = new ClientThread[4];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new ClientThread(i + 1);
            threads[i].start();
        }
        for (Thread t : threads) t.join();
    }

    // EdgeStressTest
    @Test
    public void EdgeStressTest() throws RemoteException, InterruptedException {
        String clientId = uniqueClientId("EdgeStressTest");

        // 1. Pop from empty stack
        assertThrows(Exception.class, () -> calc.pop(clientId));

        //  2. Push & pop extreme integer values
        calc.pushValue(clientId, Integer.MAX_VALUE);
        calc.pushValue(clientId, Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, calc.pop(clientId));
        assertEquals(Integer.MAX_VALUE, calc.pop(clientId));

        //  3. GCD / LCM with zero / negative numbers
        calc.pushValue(clientId, 0);
        calc.pushValue(clientId, 42);
        calc.pushOperation(clientId, "gcd");
        assertEquals(42, calc.pop(clientId));

        calc.pushValue(clientId, -12);
        calc.pushValue(clientId, 20);
        calc.pushOperation(clientId, "lcm");
        assertEquals(60, calc.pop(clientId));

        // 4. Min / Max with single-element stack
        calc.pushValue(clientId, 10);
        calc.pushOperation(clientId, "min");
        assertEquals(10, calc.pop(clientId));

        // 5. delayPop test
        calc.pushValue(clientId, 99);
        long start = System.currentTimeMillis();
        assertEquals(99, calc.delayPop(clientId, 2000));
        assertTrue(System.currentTimeMillis() - start >= 1900);

        //  6. Multi-threaded concurrency test
        Thread[] threads = new Thread[5];
        for (int i = 0; i < threads.length; i++) {
            int t = i;
            threads[i] = new Thread(() -> {
                String threadId = clientId + "-T" + t;
                try {
                    for (int j = 0; j < 100; j++) calc.pushValue(threadId, j);
                    calc.pushOperation(threadId, "max");
                    assertEquals(99, calc.pop(threadId));
                } catch (RemoteException e) { fail("Thread exception: " + e.getMessage()); }
            });
            threads[i].start();
        }
        for (Thread t : threads) t.join();

        // 7. Rapid stress push/pop
        for (int i = 0; i < 1000; i++) {
            calc.pushValue(clientId, i);
            calc.pop(clientId);
        }
    }
}
