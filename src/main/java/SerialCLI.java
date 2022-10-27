import com.fazecast.jSerialComm.SerialPort;

import java.util.Arrays;

public class SerialCLI {

    private final Console console = new Console();
    private final SerialManager serialManager = new SerialManager();


    public void execute(Enum<RunModes> runMode) {
        if (runMode.equals(RunModes.STANDARD)) {

            do {
                console.printMessage("\n" + "MESSAGE MODE: "+ serialManager.getCurrentMessageMode("short") + " | SIMPLE SERIAL COMMUNICATOR | " + "| MODE: " + runMode);
                console.printMenu();
                int selection = console.readIntegerFromInput(">>> ");
                switch(selection) {
                    case 1:
                        console.printMessage("****** AVAILABLE COM PORTS ****** \n");
                        for(SerialPort port : serialManager.getAvailablePorts()) {
                            console.printMessage(port.getSystemPortName().toUpperCase() + " | " + port.getDescriptivePortName() + "\n");
                        }
                        break;
                    case 2:
                        console.printMessage(serialManager.connect(serialManager.getPortByName(console.readStringFromInput("Enter Port Name: "))));
                        break;
                    case 3:
                        console.printErrMessage("Disconnected from last port.");
                        serialManager.disconnect();
                        break;
                    case 4:
                        console.printMessage(serialManager.checkConnection());
                        break;
                    case 5:
                        console.printMessage(serialManager.getConfig());
                        break;
                    case 6:
                        int baud = console.readIntegerFromInput("Enter Baud Rate: ");
                        int dataBits = console.readIntegerFromInput("Enter Data Bits: ");
                        int stopBits = console.readIntegerFromInput("Enter Stop Bits: ");
                        int parity = console.readIntegerFromInput("Enter Parity: ");
                        serialManager.updateConfig(baud, dataBits, stopBits, parity);
                        break;
                    case 7:
                        console.printErrMessage(serialManager.revertConfig());
                        break;
                    case 8:
                        serialManager.writeData("Test");
                        break;
                    case 0:
                        console.printErrMessage("Exiting...");
                        System.exit(0);
                    default:
                        console.printErrMessage("ERROR: Invalid Input!");
                        break;
                }
            }
            while (true);
        }

    }
}