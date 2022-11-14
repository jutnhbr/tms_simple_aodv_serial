import data.AT;
import data.ATResponse;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

public class ParseTesterThread extends Thread {

    private final SerialManager serialManager;
    private final Console console = new Console();
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private boolean sendingMode = false;
    private boolean configured = false;
    private int availableBytes;

    public ParseTesterThread(SerialManager serialManager) {
        this.serialManager = serialManager;
    }

    public synchronized void run() {
        isRunning.set(true);
        try {
            byte[] readBuffer;
            try {
                while (isRunning.get()) {
                    while (serialManager.getActivePort().bytesAvailable() == 0)
                        Thread.sleep(20);

                    readBuffer = new byte[serialManager.getActivePort().bytesAvailable()];
                    int numRead = serialManager.getActivePort().readBytes(readBuffer, readBuffer.length);
                    parseATResponse(readBuffer);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void stopThread() {
        isRunning.set(false);
    }

    public void parseATResponse(byte[] data) {

        /*
        If the last message was AT+SEND=XX then we need to wait for the data to be sent. So if there are bytes available
        parse the incoming message instead of going through the AT command switch case
         */
        if (availableBytes > 0) {
            System.out.println("Bytes available: " + availableBytes);
            // TODO: Parse received message
            availableBytes = 0;
        } else {
            // If there wasn't a recent AT+SEND command, try to parse the incoming message as an AT command
            String response = new String(data, StandardCharsets.UTF_8);
            AT at = null;
            // Try to find AT Command
            try {
                at = AT.valueOf(replaceCommand(response.trim()));
            } catch (IllegalArgumentException e) {
                console.printErrMessage("TESTER>>> Invalid AT Command: NOT FOUND");
            }
            // If AT Command exists, parse response
            if (at != null) {
                switch (at) {
                    case AT -> serialManager.writeData(ATResponse.AT_OK.getCommand());
                    case AT_VER -> serialManager.writeData(ATResponse.AT_VER.getCommand());
                    case AT_RSSI -> serialManager.writeData(ATResponse.AT_RSSI.getCommand());
                    case AT_ADDR -> serialManager.writeData(ATResponse.AT_ADDR.getCommand());
                    case AT_RX -> {
                        sendingMode = true;
                        serialManager.writeData(ATResponse.AT_OK.getCommand());
                    }
                    case AT_RST -> {
                        sendingMode = false;
                        configured = false;
                        serialManager.writeData(ATResponse.AT_OK.getCommand());
                    }
                    case AT_SEND -> {
                        if (sendingMode) {
                            String[] split = response.split("=");
                            if (split.length == 2) {
                                availableBytes = parseSendingMode(split[1]);
                            }
                        } else {
                            serialManager.writeData(ATResponse.AT_ERR.getCommand());
                        }
                    }
                    default -> serialManager.writeData(ATResponse.AT_ERR.getCommand());
                }
            } else {
                serialManager.writeData(ATResponse.AT_ERR.getCommand());
            }
        }
    }

    public String replaceCommand(String command) {
        return command.replace("+", "_").replaceAll("[0-9?+=]", "");
    }

    public int parseSendingMode(String bytes) {
        int availableBytes;
        try {
            availableBytes = Integer.parseInt(bytes.trim());
        } catch (NumberFormatException e) {
            serialManager.writeData(ATResponse.AT_ERR.getCommand());
            console.printErrMessage("TESTER>>> Invalid Sendmode Length");
            return -1;
        }
        if (availableBytes < 1 || availableBytes > 255) {
            serialManager.writeData(ATResponse.AT_ERR.getCommand());
            return -1;
        }
        return availableBytes;
    }


}
