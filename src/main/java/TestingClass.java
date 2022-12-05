import control.LoraCLI;
import model.messageTypes.RREQ;
import model.protocol.ProtocolManager;

import java.util.Base64;

public class TestingClass {

    public static void main(String[] args) throws InterruptedException {

        LoraCLI loraCLI = new LoraCLI();
        // loraCLI.start();

        ProtocolManager protocolManager = new ProtocolManager();
        RREQ rreq = protocolManager.generateRREQ((byte) 2);
        byte[] rreqBytes = protocolManager.RREQtoBytes(rreq);
        String encoded = Base64.getEncoder().withoutPadding().encodeToString(rreqBytes);
        System.out.println(encoded);
        byte[] incomingMessageBytes = Base64.getDecoder().decode(encoded);
        System.out.println(incomingMessageBytes[0]);

        /*
        byte type = 1;
        byte flags = 0;
        byte hopCount = 0;
        byte req = 0;
        byte destAddr = 7;
        byte destSeq = 0;
        byte sourceAddr = 8;
        byte sourceSeq = 0;
        // encode all to base64
        byte[] payload = new byte[]{type, flags, hopCount, req, destAddr, destSeq, sourceAddr, sourceSeq};
        System.out.println(Arrays.toString(payload));
        String encodedPayload = Base64.getEncoder().encodeToString(payload);
        // Get legnth of encoded payload
        int length = encodedPayload.length();
        System.out.println(length * 6);
        System.out.println(encodedPayload);
        // decode base64 to bytes
        byte[] decodedPayload = Base64.getDecoder().decode(encodedPayload);
        System.out.println(Arrays.toString(decodedPayload));


         */




    }

}
