import com.fazecast.jSerialComm.SerialPort;

public class SerialManager {

    private int baudRate = 9600;
    private int dataBits = 8;
    private int stopBits = 1;
    private int parity = 0;
    private final int[] standardConfig = {9600, 8, 1, 0};
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
        this.baudRate = standardConfig[0];
        this.dataBits = standardConfig[1];
        this.stopBits = standardConfig[2];
        this.parity = standardConfig[3];
        return "\nConfig reverted to standard \n";
    }


    public String connect(SerialPort port) {
        port.setBaudRate(baudRate);
        port.setNumDataBits(dataBits);
        port.setNumStopBits(stopBits);
        port.setParity(parity);
        boolean check = port.openPort();
        activePort = port;
        if (check) {
            return "\nConnected to: " + port.getSystemPortName().toUpperCase() + " | " + port.getDescriptivePortName() + "\n";
        }
        else {
            return "\nERROR: Could not connect to " + port.getSystemPortName().toUpperCase() + "\n";
        }
    }

    public void disconnect() {
        activePort.closePort();
    }

    public void writeData(String data) {
        data = messageUtil.message(data);
        activePort.writeBytes(data.getBytes(), data.length());
    }

    public String readData() {
        byte[] buffer = new byte[activePort.bytesAvailable()];
        activePort.readBytes(buffer, buffer.length);
        return new String(buffer);
    }

    public String checkConnection() {
        if(activePort == null) return "\nNo active Port! You need to connect to a port first.\n";
        if (activePort.isOpen()) {
            return  "\nConnected to " + activePort.getDescriptivePortName() + " | " + activePort.getSystemPortName().toUpperCase() + "\n";
        }
        else {
            return  "\nNot Connected\n";
        }
    }

    public String getCurrentMessageMode(String identifier) {
    	return identifier.equalsIgnoreCase("short") ? messageUtil.getCurrentMode().getModeSymbol() : messageUtil.getCurrentMode().getModeName();
    }
}


