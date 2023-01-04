/**
 * To test the ProtocolManager start the ApplicationTester.java class. This will start the TestEnvironment.java class.
 * Underneath you can configure test messages that will be sent to the ProtocolManager. Stick to a total length of
 * 72 bits per data packet. The first 6 bits are reserved for the type of message.
 */

package control;

import model.SerialManager;
import org.uncommons.maths.binary.BitString;
import view.Console;

import java.util.Base64;

public class TestEnvironment {

    private final Console console = new Console();

    public void start(SerialManager serialTester) throws InterruptedException {
        serialTester.connect(serialTester.getPortByName(console.readStringFromInput("Enter Port Name: ")));
        console.printMessage("Tester connected to "+ serialTester.getActivePort().getSystemPortName() + "\n");
        console.printMessage("Starting Parsing Thread...\n");
        // ParseTesterThread test = new ParseTesterThread(serialTester);
        // test.start();
        // console.printMessage("Tester is running ...\n");
        BitString payloadRREQ = new BitString("000001000000000001000000111111111111111100000001101011011101110100011001");
        BitString payloadRREP = new BitString("000010000000000001000000111111111111111100000001101011011101110100011001");
        String encodedPayload = Base64.getEncoder().encodeToString(payloadRREQ.toNumber().toByteArray());
        String encodedPayload2 = Base64.getEncoder().encodeToString(payloadRREP.toNumber().toByteArray());
        String lora = "LR,XXXX,XX,";
        String message1 = lora + encodedPayload;
        String message2 = lora + encodedPayload2;
        System.out.println("Payload for RREQ -->" + message1);
        System.out.println("Payload for RREP -->" + message2);
        Thread.sleep(4000);
        serialTester.writeData(message1);
        // Thread.sleep(4000);
        //serialTester.writeData(message1);
        //Thread.sleep(4000);
        //serialTester.writeData(message2);


    }


}
