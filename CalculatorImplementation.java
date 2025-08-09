import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Stack;

// Implementation of the Calculator interface for Java RMI
public class CalculatorImplementation extends UnicastRemoteObject implements Calculator {

    private final Stack<Integer> stack;

    public CalculatorImplementation() throws RemoteException {
        super();
        stack = new Stack<>();
    }

    @Override
    public synchronized void pushValue(int val) throws RemoteException {
        stack.push(val);
        System.out.println("Pushed value: " + val);
    }

    @Override
    public synchronized void pushOperation(String operator) throws RemoteException {
        if (stack.isEmpty()) {
            System.out.println("Stack is empty !!!");
            return;
        }

        int result;
        switch (operator.toLowerCase()) {
            case "min" -> result = stack.stream().min(Integer::compare).get();
            case "max" -> result = stack.stream().max(Integer::compare).get();
            case "lcm" -> {
                result = stack.pop();
                while (!stack.isEmpty()) {
                    result = lcm(result, stack.pop());
                }
            }
            case "gcd" -> {
                result = stack.pop();
                while (!stack.isEmpty()) {
                    result = gcd(result, stack.pop());
                }
            }
            default -> {
                System.out.println("( "+ operator+") is invalid operator or it is not supported in this calculator !!");
                return;
            }
        }

        stack.clear();
        stack.push(result);
        System.out.println("Operation ( " + operator + " ) result pushed: " + result);
    }

    @Override
    public synchronized int pop() throws RemoteException {
        if (stack.isEmpty()) {
            throw new RemoteException("Stack is empty !!");
        }
        int val = stack.pop();
        System.out.println("Popped value: " + val);
        return val;
    }

    @Override
    public synchronized boolean isEmpty() throws RemoteException {
        return stack.isEmpty();
    }

    @Override
    public synchronized int delayPop(int millis) throws RemoteException {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RemoteException("Thread interrupted", e);
        }
        return pop();
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
