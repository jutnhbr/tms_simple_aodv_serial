package threads;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import org.apache.commons.lang3.StringUtils;
import view.Console;

import java.util.concurrent.ConcurrentLinkedQueue;


public class Listener implements SerialPortDataListener {

    private final ConcurrentLinkedQueue<String> commandQueue;
    private final Console console = new Console();

    public Listener(ConcurrentLinkedQueue<String> commandQueue) {
        this.commandQueue = commandQueue;
    }

    String str = "";

    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {

        byte[] buffer = new byte[serialPortEvent.getSerialPort().bytesAvailable()];
        serialPortEvent.getSerialPort().readBytes(buffer, buffer.length);
        str += new String(buffer);
        if (!str.contains("\r\n")) {
            return;
        } else {
            buffer = str.getBytes();
            str = "";
        }
        String response = StringUtils.substringBefore(new String(buffer), "\r");
        if (response.contains("AT,")) {
            console.printMessage("Listener >>> Received AT Command. Not adding to queue." + "\n");
        } else {
            console.printMessage("Listener >>> Received Payload " + response + " Added to queue" + "\n");
            commandQueue.add(response);
        }
    }
}