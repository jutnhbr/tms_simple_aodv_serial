package control;

import com.fazecast.jSerialComm.SerialPort;
import data.AT;
import model.SerialManager;
import model.messageTypes.RREQ;
import model.protocol.ProtocolManager;
import threads.ReadingThread;
import view.Console;
import view.MenuFactory;

public class LoraCLI {

    private final Console console = new Console();
    private final SerialManager serialManager = new SerialManager();
    private final ReadingThread readingThread = new ReadingThread(serialManager, true);
    private final MenuFactory menuFactory = new MenuFactory();
    private final ProtocolManager protocolManager = new ProtocolManager(serialManager);

    public synchronized void start() throws InterruptedException {


        do {
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
                        break;
                    } else {
                        serialManager.connect(serialManager.getPortByName(console.readStringFromInput("Enter Port Name: ")));
                        console.printMessage(serialManager.getActivePort().getSystemPortName().toUpperCase() + " | " + serialManager.getActivePort().getDescriptivePortName() + " is now open.\n");
                        readingThread.start();
                    }
                    break;
                case 3:
                    console.printMessage("Configuring RX Mode ... \n");
                    serialManager.writeData(AT.AT_RX.getCommand());
                    Thread.sleep(2500);
                    console.printMessage("\nConfiguring Config String ...\n");
                    serialManager.writeData(serialManager.getATConfigString());
                    Thread.sleep(2500);
                    break;
                case 4:
                    String destAddr = console.readStringFromInput("Enter Destination Address (e.g. AAAA): ");
                    RREQ req = protocolManager.generateRREQ(destAddr);
                    protocolManager.sendRREQBroadcast(req);
                    break;
                case 5:
                    console.printMessage(
                            "****************************ROUTING TABLE****************************\n"
                            + protocolManager.getRoutingTableManager().getRoutingTable().toString()
                            + "\n************************REVERSE ROUTING TABLE************************\n"
                            + protocolManager.getRoutingTableManager().getReverseRoutingTable().toString()
                            + "\n*********************************************************************\n");
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
        while(true);
    }
}


