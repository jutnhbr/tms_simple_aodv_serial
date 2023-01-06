package threads;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import org.apache.commons.lang3.StringUtils;


import java.nio.charset.StandardCharsets;

public class  Listener implements SerialPortDataListener {


    String str = "";
    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {

        byte[] buffer = new byte[serialPortEvent.getSerialPort().bytesAvailable()];
        serialPortEvent.getSerialPort().readBytes(buffer,buffer.length);
        //System.out.println(new String(buffer));
        //System.out.println("RESPONSE");
        str += new String(buffer);
        if(!str.contains("\r\n")){
            return;
        }else {
            buffer = str.getBytes();
            str = "";
        }
        System.out.println("RESPONSE");
        String response = StringUtils.substringBefore(new String(buffer),"\r");
        System.out.println(response);



    }
}