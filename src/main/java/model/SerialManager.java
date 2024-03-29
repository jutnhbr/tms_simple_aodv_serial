package model;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import data.AT;
import threads.Listener;
import util.MessageUtil;

import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SerialManager {

    // PORT CONFIG
    private int baudRate = 115200;
    private int dataBits = 8;
    private int stopBits = 1;
    private int parity = 0;
    private final int[] standardPortConfig = {115200, 8, 1, 0};
    // AT COMMAND CONFIG
    private final String ATConfigString = AT.AT_CFG.getCommand() + "433920000,5,7,7,4,1,0,0,0,0,3000,8,4";

    private final ConcurrentLinkedQueue<String> commandQueue = new ConcurrentLinkedQueue<>();
    // Port
    private SerialPort activePort;
    // Util
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
        return "SerialManager >>> " +
                "\nBaud Rate: " + baudRate +
                "\nData Bits: " + dataBits +
                "\nStop Bits: " + stopBits +
                "\nParity: " + parity + "\n";
    }

    public String revertConfig() {
        this.baudRate = standardPortConfig[0];
        this.dataBits = standardPortConfig[1];
        this.stopBits = standardPortConfig[2];
        this.parity = standardPortConfig[3];
        return "\nSerialManager >>> Config reverted to standard \n";
    }



    public String connect(SerialPort port) {
        if (port.isOpen()) {
            return "SerialManager >>> Port is already open.";
        }
        port.setBaudRate(baudRate);
        port.setNumDataBits(dataBits);
        port.setNumStopBits(stopBits);
        port.setParity(parity);
        activePort = port;
        boolean check = activePort.openPort();
        if (check) {
            Listener listener = new Listener(commandQueue);
            activePort.addDataListener(listener);
            return "\nSerialManager >>> Connected to: " + activePort.getSystemPortName().toUpperCase() + " | " + activePort.getDescriptivePortName() + "\n";
        } else {
            return "\nSerialManager >>> ERROR: Could not connect to " + activePort.getSystemPortName().toUpperCase() + "\n";
        }
    }

    public void disconnect() {
        activePort.closePort();
    }

    public void writeData(String data) throws NullPointerException {
        if (activePort == null) throw new NullPointerException("SerialManager >>> ERROR: No active port!");
        data = messageUtil.parseMessage(data);
        System.out.println("SerialManager >>> Writing Data to Port:" + data);
        activePort.writeBytes(data.getBytes(), data.length());
    }

    public String checkConnection() throws NullPointerException {
        if (activePort == null)
            throw new NullPointerException("\nSerialManager >>> No active Port! You need to connect to a port first.\n");
        if (activePort.isOpen()) {
            return "\nSerialManager >>> Connected to " + activePort.getDescriptivePortName() + " | " + activePort.getSystemPortName().toUpperCase() + "\n";
        } else {
            return "\nSerialManager >>> Not Connected\n";
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

    public ConcurrentLinkedQueue<String> getCommandQueue() {
        return commandQueue;
    }
}


