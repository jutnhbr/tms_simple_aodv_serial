import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReadingThread extends Thread {

    private SerialManager serialManager;
    private final Console console = new Console();
    private final AtomicBoolean isRunning = new AtomicBoolean(false);


    public ReadingThread(SerialManager serialManager) {
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
                    // System.out.println("Read " + numRead + " bytes.");
                    console.printMessage(new String(readBuffer, StandardCharsets.UTF_8));
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
