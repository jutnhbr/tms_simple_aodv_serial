import com.fazecast.jSerialComm.SerialPort;
import data.AT;

public class SerialManager {

    // PORT CONFIG
    private int baudRate = 115200;
    private int dataBits = 8;
    private int stopBits = 1;
    private int parity = 0;
    private final int[] standardPortConfig = {115200, 8, 1, 0};
    // AT COMMAND CONFIG

    private String ATConfigString = AT.AT_CFG.getCommand() + "433000000,5,9,9,4,1,0,0,0,0,4000,8,4";

    private SerialPort activePort;
    private final MessageUtil messageUtil = new MessageUtil();


    public SerialManager() {
    }


    public SerialPort[] getAvailablePorts() {
        return SerialPort.getCommPorts();
    }

    public SerialPort getPortByName(String portName) {
        return SerialPort.getCommPort(portName);
        }

    public void updateConfig(int baudRate, int dataBits, int stopBits, int parity) {
        this.baudRate = baudRate;
        this.dataBits = dataBits;
        this.stopBits = stopBits;
        this.parity = parity;
    }
    public String getConfig() {
        return "\nBaud Rate: " + baudRate +
                "\nData Bits: " + dataBits +
                "\nStop Bits: " + stopBits +
                "\nParity: " + parity + "\n";
    }

    public String revertConfig() {
        this.baudRate = standardPortConfig[0];
        this.dataBits = standardPortConfig[1];
        this.stopBits = standardPortConfig[2];
        this.parity = standardPortConfig[3];
        return "\nConfig reverted to standard \n";
    }


    public String connect(SerialPort port) {
        if(port.isOpen()) {
            return "Port is already open.";
        }
        port.setBaudRate(baudRate);
        port.setNumDataBits(dataBits);
        port.setNumStopBits(stopBits);
        port.setParity(parity);
        activePort = port;
        boolean check = activePort.openPort();
        if (check) {
            return "\nConnected to: " + activePort.getSystemPortName().toUpperCase() + " | " + activePort.getDescriptivePortName() + "\n";
        }
        else {
            return "\nERROR: Could not connect to " + activePort.getSystemPortName().toUpperCase() + "\n";
        }
    }

    public void disconnect() {
        activePort.closePort();
    }

    public void writeData(String data) throws NullPointerException {
        if(activePort == null) throw new NullPointerException("ERROR: No active port!");
        data = messageUtil.parseMessage(data);
        System.out.println("Writing Data:" + data);
        activePort.writeBytes(data.getBytes(), data.length());
    }

    public String checkConnection() throws NullPointerException {
        if(activePort == null) throw new NullPointerException("\nNo active Port! You need to connect to a port first.\n");
        if (activePort.isOpen()) {
            return  "\nConnected to " + activePort.getDescriptivePortName() + " | " + activePort.getSystemPortName().toUpperCase() + "\n";
        }
        else {
            return  "\nNot Connected\n";
        }
    }

    public String getCurrentMessageMode(String identifier) {
    	return identifier.equalsIgnoreCase("short") ? messageUtil.getCurrentMode().getModeSymbolAsString() : messageUtil.getCurrentMode().getModeName();
    }

    public String getATConfigString() {
        return ATConfigString;
    }

    public SerialPort getActivePort() {
        return activePort;
    }
}


