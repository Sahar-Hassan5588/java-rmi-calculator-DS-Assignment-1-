import java.rmi.Naming;
import java.util.Scanner;

// A client app to test the Calculator RMI service
public class CalculatorClient {
    public static void main(String[] args) throws Exception {
        // Connect to the Calculator service on localhost
        Calculator calc = (Calculator) Naming.lookup("rmi://localhost/Calculator");
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.println("\n Welcome to the Calculator Client.\n You can perform the following commands: ");
            System.out.println("1. Push values onto the stack");
            System.out.println("2. Perform an operation (min, max, gcd, lcm)");
            System.out.println("3. Pop value from the stack");
            System.out.println("4. Delay then pop value");
            System.out.println("5. Check if stack is empty");
            System.out.println("6. Exit");
            System.out.print("Select an option from 1 to 6: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.print("\nEnter integers values separated by commas (such as 5,10,15): ");
                    String input = scanner.nextLine();
                    String[] parts = input.split(",");
                    int count = 0;
                    for (String part : parts) {
                        try {
                            int val = Integer.parseInt(part.trim());
                            calc.pushValue(val);
                            count++;
                        } catch (NumberFormatException e) {
                            System.out.println("\nSkipping invalid input: '" + part.trim() + "'");
                        }
                    }
                    System.out.println("\nSuccessfully pushed " + count + " value(s).");
                    break;

                case "2":
                    if (calc.isEmpty()) {
                        System.out.println("\nStack is empty. Please push values first.");
                        break;
                    }
                    System.out.println("Available operations:");
                    System.out.println(" - min  >> returns the minimum value among all pushed values");
                    System.out.println(" - max  >> returns the maximum value among all pushed values");
                    System.out.println(" - gcd  >> returns the greatest common divisor of all pushed values");
                    System.out.println(" - lcm  >> returns the least common multiple of all pushed values");

                    System.out.print("Enter operation: ");
                    String operation = scanner.nextLine().trim().toLowerCase();
                    if (!operation.equals("min") && !operation.equals("max") && !operation.equals("gcd") && !operation.equals("lcm")) {
                        System.out.println("\nInvalid operation. Please select one from the list.");
                        break;
                    }
                    calc.pushOperation(operation);
                    System.out.println("\nOperation '" + operation + "' performed.");
                    System.out.println("Result has been pushed to the stack. (Use 'Pop' to retrieve it.)");
                    break;

                case "3":
                    try {
                        int val = calc.pop();
                        System.out.println("\nPopped value: " + val);
                    } catch (Exception e) {
                        System.out.println("\nStack is empty. Nothing to pop.");
                    }
                    break;

                case "4":
                    System.out.print("\nEnter delay in milliseconds (such as 3000 for 3 seconds): ");
                    String delayStr = scanner.nextLine().trim();
                    try {
                        int delay = Integer.parseInt(delayStr);
                        int val = calc.delayPop(delay);
                        System.out.println("\nAfter delay, popped value: " + val);
                    } catch (NumberFormatException e) {
                        System.out.println("\nInvalid input. Please enter a valid number.");
                    } catch (Exception e) {
                        System.out.println("\nStack is empty. Nothing to pop.");
                    }
                    break;

                case "5":
                    boolean empty = calc.isEmpty();
                    System.out.println("\nStack is empty? " + empty);
                    break;

                case "6":
                    System.out.println("\nGoodbye!");
                    scanner.close();
                    return;

                default:
                    System.out.println("\nInvalid selection. Please enter a number from 1 to 6.");
            }
        }
    }
}
