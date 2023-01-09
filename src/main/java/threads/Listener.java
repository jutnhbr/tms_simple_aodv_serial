package threads;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ConcurrentLinkedQueue;


public class  Listener implements SerialPortDataListener {

    private final ConcurrentLinkedQueue<String> commandQueue;

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
        serialPortEvent.getSerialPort().readBytes(buffer,buffer.length);
        str += new String(buffer);
        if(!str.contains("\r\n")){
            return;
        }else {
            buffer = str.getBytes();
            str = "";
        }
        String response = StringUtils.substringBefore(new String(buffer),"\r");
        System.out.println(response);
        if(response.contains("AT,")){
            System.out.println("Listener >>> Received AT Command. Not adding to queue.");
        } else {
            System.out.println("Listener >>> Received Payload " + response + " Added to queue");
            commandQueue.add(response);
        }



    }
}