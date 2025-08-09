import java.rmi.Remote;
import java.rmi.RemoteException;

//Calculator interface defines the remote methods that clients can call.
public interface Calculator extends Remote {
    /*
      Push an integer value onto the stack.
      @param val the value to push onto the stack.
      void no return value
      @throws RemoteException if remote communication fails
     */
    void pushValue(int val) throws RemoteException;

    /*
      Push an operation onto the stack.
      Expected operators: min, max, lcm, gcd
      This will pop all values on the stack, apply the operation,
      and push the result back.
      @param operator the operation to perform.
      void no return value
      @throws RemoteException if remote communication fails.
     */
    void pushOperation(String operator) throws RemoteException;

    /*
      Pop the top value from the stack.
      @return the popped integer value
      @throws RemoteException if remote communication fails
     */
    int pop() throws RemoteException;

    /*
      Check if the stack is empty.
      @return true if empty, false otherwise
      @throws RemoteException if remote communication fails
     */
    boolean isEmpty() throws RemoteException;

    /*
      Wait for a given time in milliseconds, then pop the top value.
      @param millis time to wait in milliseconds
      @return the popped integer value
      @throws RemoteException if remote communication fails
     */
    int delayPop(int millis) throws RemoteException;
}
