package threads;

import model.SerialManager;
import view.Console;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReadingThread extends Thread {

    private final SerialManager serialManager;
    private final Console console = new Console();
    private final AtomicBoolean isRunning = new AtomicBoolean(false);


    public ReadingThread(SerialManager serialManager, boolean aodvFlag) {
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

                    // Read data from serial port
                    readBuffer = new byte[serialManager.getActivePort().bytesAvailable()];
                    int numRead = serialManager.getActivePort().readBytes(readBuffer, readBuffer.length);
                    {
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

}
