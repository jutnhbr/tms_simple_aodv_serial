package threads;

import model.SerialManager;
import view.Console;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReadingThread extends Thread {

    private final SerialManager serialManager;
    private final Console console = new Console();
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final ConcurrentLinkedQueue<String> commandQueue = new ConcurrentLinkedQueue<>();
    private boolean AODV = false;


    public ReadingThread(SerialManager serialManager, boolean aodvFlag) {
        this.serialManager = serialManager;
        this.AODV = aodvFlag;
    }


    public synchronized void run() {
        isRunning.set(true);
        try {
            byte[] readBuffer;
            try {
                while (isRunning.get()) {
                    while (serialManager.getActivePort().bytesAvailable() == 0)
                        Thread.sleep(20);

                    // Read data from serial port
                    readBuffer = new byte[serialManager.getActivePort().bytesAvailable()];
                    int numRead = serialManager.getActivePort().readBytes(readBuffer, readBuffer.length);

                    // Write data to queue if AODV is enabled
                    if (AODV) {
                        commandQueue.add(new String(readBuffer, StandardCharsets.UTF_8));
                    } else {
                        console.printMessage(new String(readBuffer, StandardCharsets.UTF_8));
                    }
                }
            } catch (Exception ignored) {

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void stopThread() {
        isRunning.set(false);
    }

    public ConcurrentLinkedQueue<String> getCommandQueue() {
        return commandQueue;
    }
}
