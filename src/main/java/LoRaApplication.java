/**
 * Class to start the LoRa application. This class is responsible for starting the LoRa Protocol Implementation. Start
 * the TestingClass.java to receive predefined messages.
 */

import control.LoraCLI;

public class LoRaApplication {

    public static void main(String[] args) throws InterruptedException {
        LoraCLI loraCLI = new LoraCLI();
        loraCLI.start();
    }

}
