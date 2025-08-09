import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

// Starts the RMI registry and binds the Calculator service.
public class CalculatorServer {
    /*  -Starts the RMI registry using the default RMI port 1099
        -then creates an instance of CalculatorImplementation
        -Registers (binds) the calculator object with a name in the RMI registry 
        -print message shows that server running
    */
    public static void main(String[] args) throws Exception {
            // Start RMI registry
            final int RMI_PORT = 1099;
            LocateRegistry.createRegistry(RMI_PORT);
            CalculatorImplementation calc = new CalculatorImplementation();
            Naming.bind("Calculator", calc);
            System.out.println("Calculator server is running...");
    }
}
