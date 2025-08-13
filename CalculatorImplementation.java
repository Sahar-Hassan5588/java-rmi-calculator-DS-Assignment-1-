import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

// Implementation of the Calculator interface for Java RMI
public class CalculatorImplementation extends UnicastRemoteObject implements Calculator {

    private final Map<String, Stack<Integer>> clientStacks;

    public CalculatorImplementation() throws RemoteException {
        super();
        clientStacks = new ConcurrentHashMap<>();
    }

    // Helper to create/get a stack for clientId
    private Stack<Integer> getStack(String clientId) {
        return clientStacks.computeIfAbsent(clientId, k -> new Stack<>());
    }

    @Override
    public void pushValue(String clientId, int val) throws RemoteException {
        Stack<Integer> stack = getStack(clientId);
        synchronized (stack) {
        stack.push(val);
        }
        System.out.println("Client " + clientId + ": Pushed value: " + val);
    }

    @Override
    public void pushOperation(String clientId, String operator) throws RemoteException {
        Stack<Integer> stack = getStack(clientId);
        synchronized(stack){
        if (stack.isEmpty()) {
            System.out.println("Client " + clientId + ": Stack is empty !!!");
            return;
        }

        int result;
        switch (operator.toLowerCase()) {
            case "min":
                result = stack.stream().min(Integer::compare).get();break;
            case "max":
                result = stack.stream().max(Integer::compare).get();break;
            case "lcm":{
                result = stack.pop();
                while (!stack.isEmpty()) {
                    result = lcm(result, stack.pop());
                }
                break;
            }
            case "gcd": {
                result = stack.pop();
                while (!stack.isEmpty()) {
                    result = gcd(result, stack.pop());
                }
                break;
            }
            default: {
                System.out.println("Client " + clientId + ": ( "+ operator+") is invalid operator or it is not supported in this calculator !!");
                return;
            }
        }

        stack.clear();
        stack.push(result);
        System.out.println("Client " + clientId +" : Operation ( " + operator + " ) result pushed: " + result);
        }
        
    }


    @Override
    public int pop(String clientId) throws RemoteException {
        Stack<Integer> stack = getStack(clientId);
        synchronized(stack){
        if (stack.isEmpty()) {
            throw new RemoteException("Client " + clientId + ": "+"Stack is empty !!");
        }
        int val = stack.pop();
        System.out.println("Client " + clientId + ": "+ "Popped value: " + val);
        return val;
        }
    }

    @Override
    public boolean isEmpty(String clientId) throws RemoteException {
        Stack<Integer> stack = getStack(clientId);
        synchronized(stack){
        return stack.isEmpty();
        }
    }

    @Override
    public int delayPop(String clientId, int millis) throws RemoteException {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RemoteException("Thread interrupted", e);
        }
        Stack<Integer> stack = getStack(clientId);
        synchronized (stack) {
        if (stack.isEmpty()) throw new RemoteException("Empty");
        return stack.pop();
        }
    }

    /*
     Calculate the Greatest Common Divisor (gcd) of two integers using recursion.
     The gcd is the largest positive integer that divides both numbers without a remainder.
     @param a first integer
     @param b second integer
     @return the gcd of a and b
    */
    private int gcd(int a, int b) {
        return b == 0 ? Math.abs(a) : gcd(b, a % b);
    }
    /*
    Calculate the Least Common Multiple (lcm) of two integers.
    The lcm is the smallest positive integer that is divisible by both numbers.
    Uses the relationship: lcm(a,b) = |a * b| / gcd(a,b)
    @param a first integer
    @param b second integer
    @return the lcm of a and b
    */
    private int lcm(int a, int b) {
        return Math.abs(a * b) / gcd(a, b);
    }
}
