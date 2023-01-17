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

       /* String destAddr = "0008";
        String sourceAddr = "0003";
        int destSeq_ = 1;
        int sourceSeq_ = 25;
        String destAddrBinary = String.format("%16s", Integer.toBinaryString(Integer.parseInt(destAddr, 16))).replace(' ', '0');
        String sourceAddrBinary = String.format("%16s", Integer.toBinaryString(Integer.parseInt(sourceAddr, 16))).replace(' ', '0');
        String d_seq = String.format("%8s", Integer.toBinaryString(destSeq_)).replace(' ', '0');
        String s_seq = String.format("%8s", Integer.toBinaryString(sourceSeq_)).replace(' ', '0');
        BitString type = new BitString("000001");
        BitString flags = new BitString("000000");
        BitString hopCount = new BitString("000001");
        BitString reqID = new BitString("000000");
        BitString destAddrS = new BitString(destAddrBinary);
        BitString destSeq = new BitString(d_seq);
        BitString sourceAddrS = new BitString(sourceAddrBinary);
        BitString sourceSeq = new BitString(s_seq);
        BitString payloadForRREQ = new BitString(type.toString() + flags + hopCount + reqID + destAddrS + destSeq + sourceAddrS + sourceSeq);

        String encodedPayload = Base64.getEncoder().encodeToString(payloadForRREQ.toNumber().toByteArray());*/

        String encodedPayload= "AAAVIYWxsbw==";

        String lora = "LR,0003,XX,";

        String message1 = lora + encodedPayload;

        System.out.println("Payload for RREQ -->" + message1);
        Thread.sleep(4000);
        serialTester.writeData(message1);



    }


}
