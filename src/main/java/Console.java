import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.Scanner;

public class Console {

    private final LinkedList<String> menu = menuBuilder();

    public int readIntegerFromInput(String text) throws InputMismatchException {
        printMessage(text);
        Scanner intInputSc = new Scanner(System.in);

        while(true) {
            try {
                return intInputSc.nextInt();
            }
            catch (InputMismatchException e) {
                printErrMessage("ERROR: Only Integers are allowed!");
                intInputSc.nextLine();
            }
        }
    }

    public String readFirstCharFromInput(String text) throws InputMismatchException {
        printMessage(text);
        char charInput;
        String output;
        Scanner charInputSc = new Scanner(System.in);

        while(true) {
            try {
                charInput = charInputSc.next().toLowerCase().charAt(0);
                output = String.valueOf(charInput);
                return output;

            }
            catch (InputMismatchException | NullPointerException e) {
                printErrMessage("ERROR: Only Chars are allowed!");
                charInputSc.nextLine();
            }
        }
    }

    public String readStringFromInput(String text) throws InputMismatchException {
        printMessage(text);
        Scanner StringInputSc = new Scanner(System.in);

        while (true) {
            try {
                return StringInputSc.nextLine().toLowerCase();

            }
            catch (InputMismatchException e) {
                printErrMessage("ERROR: Only Strings are allowed!");
                StringInputSc.nextLine();

            }
        }
    }
    public void printMessage(String message) {
        System.out.print(message);
    }
    public void printErrMessage(String error) {
        System.err.println(error);
    }


    public void printMenu() {
        printMessage("\n");
        for(String s : menu) {
            printMessage(s + "\n");
        }
    }

    public LinkedList<String> menuBuilder() {
        LinkedList<String> menu = new LinkedList<>();

        menu.add("1: Show COM Ports");
        menu.add("2: Connect to COM Port");
        menu.add("3: Disconnect from COM Port");
        menu.add("4: Check Connection Status");
        menu.add("5: Check Port Configuration");
        menu.add("6: Edit Port Configuration");
        menu.add("7: Revert Configuration");
        menu.add("8: Send Test String");
        menu.add("0: Exit");

        return menu;
    }

}
