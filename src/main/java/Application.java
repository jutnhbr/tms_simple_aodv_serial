/**
 * Class to start the normal serial communication manager. Not used for the LoRa communication. Can be used to test
 * the serial communication, communicate with the LoRa module and send, receive and parse messages to and from
 * the LoRa module.
 */

import control.SerialCLI;
import data.RunModes;

public class Application {

    public static void main(String[] args) throws InterruptedException {

        SerialCLI serialCLI = new SerialCLI();
        serialCLI.execute(RunModes.STANDARD);

    }
}
