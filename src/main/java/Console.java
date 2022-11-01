import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.Scanner;

public class Console {


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
                return StringInputSc.nextLine();

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

    public void printMenu(LinkedList<String> menu) {
        printMessage("\n");
        for(String s : menu) {
            printMessage(s + "\n");
        }
    }

}
