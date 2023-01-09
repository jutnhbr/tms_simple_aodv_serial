/**
 * LoraCLI is a command line interface for the LoraWAN protocol. The Protocol logic starts here.
 *
 */
package control;

import com.fazecast.jSerialComm.SerialPort;
import data.AT;
import model.SerialManager;
import model.protocol.ProtocolManager;
import threads.ProtocolThread;
import threads.ReadingThread;
import view.Console;
import view.MenuFactory;

public class LoraCLI {

    private final Console console = new Console();
    private final SerialManager serialManager = new SerialManager();
    private final MenuFactory menuFactory = new MenuFactory();
    private final ProtocolManager protocolManager = new ProtocolManager(serialManager);
    private final ProtocolThread protocolThread = new ProtocolThread(protocolManager);

    public synchronized void start() throws InterruptedException {


        while(true) {
            console.printMessage("\n" + "SERIAL LORA COMMUNICATOR");
            console.printMenu(menuFactory.menuBuilder("lora"));
            int selection = console.readIntegerFromInput(">>> ");
            switch (selection) {
                case 1:
                    console.printMessage("****** AVAILABLE COM PORTS ****** \n");
                    for (SerialPort port : serialManager.getAvailablePorts()) {
                        console.printMessage(port.getSystemPortName().toUpperCase() + " | " + port.getDescriptivePortName() + "\n");
                    }
                    break;
                case 2:
                    if (serialManager.getActivePort() != null && serialManager.getActivePort().isOpen()) {
                        console.printErrMessage("Port is already open!\n");
                    } else {
                        serialManager.connect(serialManager.getPortByName(console.readStringFromInput("Enter Port Name: ")));
                        console.printMessage(serialManager.getActivePort().getSystemPortName().toUpperCase()
                                + " | " + serialManager.getActivePort().getDescriptivePortName() + " is now open.\n");
                    }
                    break;
                case 3:
                    console.printMessage("\nConfiguring Config String ...\n");
                    serialManager.writeData(serialManager.getATConfigString());
                    Thread.sleep(2000);
                    console.printMessage("\nSet Dest Address to FFFF ...\n");
                    serialManager.writeData("AT+DEST=FFFF");
                    Thread.sleep(2000);
                    protocolThread.start();
                    console.printMessage("Configuring RX Mode ... \n");
                    serialManager.writeData(AT.AT_RX.getCommand());
                    Thread.sleep(2000);
                    break;
                case 4:
                    String destAddr = console.readStringFromInput("Enter Destination Address (e.g. AAAA): ");
                    protocolManager.generateRREQ(destAddr);
                    break;
                case 5:
                    console.printMessage(
                            "****************************ROUTING TABLE****************************\n"
                            + protocolManager.getRoutingTableManager().getRoutingTable().toString()
                            + "\n************************REVERSE ROUTING TABLE************************\n"
                            + protocolManager.getRoutingTableManager().getReverseRoutingTable().toString()
                            + "\n*********************************************************************\n");
                    break;
                case 6:
                    String data = console.readStringFromInput("Enter Data: ");
                    String dest = console.readStringFromInput("Enter Destination Address (e.g. AAAA): ");
                    protocolManager.generateDATA(data, dest);
                    break;
                case 7:
                    serialManager.writeData(AT.AT.getCommand());
                    break;
                case 0:
                    console.printErrMessage("Exiting...");
                    System.exit(0);
                    break;
                default:
                    console.printErrMessage("ERROR: Invalid Input!");
                    break;


            }
        }
    }
}


