package control;

import model.SerialManager;
import threads.ParseTesterThread;
import view.Console;

public class TestEnvironment {

    private final Console console = new Console();

    public void start(SerialManager serialTester) throws InterruptedException {
        serialTester.connect(serialTester.getPortByName(console.readStringFromInput("Enter Port Name: ")));
        console.printMessage("Tester connected to "+ serialTester.getActivePort().getSystemPortName() + "\n");
        console.printMessage("Starting Parsing Thread...\n");
        ParseTesterThread test = new ParseTesterThread(serialTester);
        test.start();
        console.printMessage("Tester is running ...\n");





    }


}
