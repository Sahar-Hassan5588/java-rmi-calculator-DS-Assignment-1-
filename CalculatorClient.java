import java.rmi.Naming;

// A client app to test the Calculator RMI service
public class CalculatorClient {
    public static void main(String[] args) throws Exception {
        // Connect to the Calculator service on localhost
        Calculator calc = (Calculator) Naming.lookup("rmi://localhost/Calculator");

        // Test gcd
        System.out.println("Pushing values: 18 and 9");
        calc.pushValue(18);
        calc.pushValue(9);
        System.out.println("Performing gcd");
        calc.pushOperation("gcd");
        System.out.println("Popped result: " + calc.pop());

        // Test lcm
        System.out.println("\nPushing values 6, 8,& 12");
        calc.pushValue(6);
        calc.pushValue(8);
        calc.pushValue(12);
        System.out.println("Performing lcm");
        calc.pushOperation("lcm");
        System.out.println("Popped result: " + calc.pop());

        // Test min
        System.out.println("\nPushing values 2,8,& 4");
        calc.pushValue(2);
        calc.pushValue(8);
        calc.pushValue(4);
        System.out.println("Performing min");
        calc.pushOperation("min");
        System.out.println("Popped result: " + calc.pop());

        // Test max
        System.out.println("\nPushing values 2,8,& 4");
        calc.pushValue(2);
        calc.pushValue(8);
        calc.pushValue(4);
        System.out.println("Performing max");
        calc.pushOperation("max");
        System.out.println("Popped result: " + calc.pop());

        // Test delayPop
        System.out.println("\nPushing value 7");
        calc.pushValue(7);
        System.out.println("Performing delayPop (5s)");
        System.out.println("Result: " + calc.delayPop(5000));

        // Test isEmpty
        System.out.println("\nChecking if stack is empty !! ");
        System.out.println("Is the stack empty? " + calc.isEmpty());
    }
}
