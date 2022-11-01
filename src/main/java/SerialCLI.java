import com.fazecast.jSerialComm.SerialPort;
import data.AT;
import data.RunModes;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class SerialCLI {

    private final Console console = new Console();
    private final SerialManager serialManager = new SerialManager();
    private final MenuFactory menuFactory = new MenuFactory();


    public void execute(Enum<RunModes> runMode) throws InterruptedException {
        if (runMode.equals(RunModes.STANDARD)) {

            do {
                console.printMessage("\n" + "MESSAGE MODE: "+ serialManager.getCurrentMessageMode("short") + " | SIMPLE SERIAL COMMUNICATOR | " + "| MODE: " + runMode);
                console.printMenu(menuFactory.menuBuilder("cli"));
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
                        try {
                            console.printMessage(serialManager.checkConnection());
                        } catch (NullPointerException e) {
                            console.printErrMessage(e.getMessage());
                        }
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
                        try {
                            String msg = console.readStringFromInput("Enter Message or type 'atmode' for specific AT Commands / 'config' for auto config: ");
                            if(msg.equalsIgnoreCase("ATMODE")) {
                                // Print AT Commands
                                console.printMenu(menuFactory.menuBuilder("at"));
                                // Send AT Command
                                serialManager.writeData(AT.values()[console.readIntegerFromInput(">>> ")].getCommand());
                                // TODO: Wait for Response
                            }
                            else if(msg.equalsIgnoreCase("config")) {
                                console.printMessage("Configuring RX Mode ... \n");
                                serialManager.writeData(AT.AT_RX.getCommand());
                                Thread.sleep(1000);
                                console.printMessage(serialManager.readData());
                                console.printMessage("\nConfiguring Config String ...\n");
                                serialManager.writeData(serialManager.getATConfigString());
                                Thread.sleep(1000);
                            }
                            else {
                                serialManager.writeData(msg);
                            }
                        } catch (NullPointerException e) {
                            console.printErrMessage(e.getMessage());
                        }
                        Thread.sleep(1000);
                        console.printMessage("Response: " + serialManager.readData());
                        break;
                    case 9:
                        // TODO: proper reading mode
                        console.printMessage("Response: " + serialManager.readData());
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