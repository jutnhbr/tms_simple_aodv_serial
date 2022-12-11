import control.LoraCLI;
import model.messageTypes.RREQ;
import model.protocol.ProtocolManager;
import org.uncommons.maths.binary.BitString;


import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

public class TestingClass {

    public static void main(String[] args) throws InterruptedException {
        LoraCLI loraCLI = new LoraCLI();
        // loraCLI.start();

        String destAddr = "FFFF";
        String sourceAddr = "ADDD";
        int destSeq_ = 1;
        int sourceSeq_ = 25;
        // Convert destAddr to binary string
        String destAddrBinary = String.format("%16s", Integer.toBinaryString(Integer.parseInt(destAddr, 16))).replace(' ', '0');
        // Convert sourceAddr to binary string
        String sourceAddrBinary = String.format("%16s", Integer.toBinaryString(Integer.parseInt(sourceAddr, 16))).replace(' ', '0');


        String d_seq = String.format("%8s", Integer.toBinaryString(destSeq_)).replace(' ', '0');
        String s_seq = String.format("%8s", Integer.toBinaryString(sourceSeq_)).replace(' ', '0');
        System.out.println("Dest Seq Binary: " + d_seq);
        System.out.println("Source Seq Binary: " + s_seq);





        BitString type = new BitString("000010");
        BitString flags = new BitString("000000");
        BitString hopCount = new BitString("000001");
        BitString reqID = new BitString("000000");
        BitString destAddrS = new BitString(destAddrBinary);
        BitString destSeq = new BitString(d_seq);
        BitString sourceAddrS = new BitString(sourceAddrBinary);
        BitString sourceSeq = new BitString(s_seq);


        BitString payload = new BitString(type.toString() + flags + hopCount + reqID + destAddrS + destSeq + sourceAddrS + sourceSeq);
        // get total length of payload
        int payloadLength = payload.getLength();
        // print payload
        System.out.println("Payload Binary: "+ payload);
        System.out.println("Payload Length: " + payloadLength);
        // encode payload to base64
        String encodedPayload = Base64.getEncoder().encodeToString(payload.toNumber().toByteArray());
        // print encoded payload
        System.out.println("Encoded Base64 String: " + encodedPayload);

        // decode payload from base64
        byte[] decodedPayload = Base64.getDecoder().decode(encodedPayload);
        // print decoded payload
        System.out.println("Decoded Payload Bytes: " + Arrays.toString(decodedPayload));
        String binaryStr = new BigInteger(1, decodedPayload).toString(2);
        System.out.println("Decoded Payload Binary: " + binaryStr);
        System.out.println("Decoded Payload Length: " + binaryStr.length());
        System.out.println("*******************************************");
        // get last 8 bits
        String srcSeq = binaryStr.substring(binaryStr.length() - 8);
        System.out.println("Source Seq " + srcSeq);
        // get next 16 bits
        String srcAddr = binaryStr.substring(binaryStr.length() - 24, binaryStr.length() - 8);
        System.out.println("Source Addr " + srcAddr);
        // get next 8 bits
        String destiSeq = binaryStr.substring(binaryStr.length() - 32, binaryStr.length() - 24);
        System.out.println("Dest Seq " + destiSeq);
        // get next 16 bits
        String destiAddr = binaryStr.substring(binaryStr.length() - 48, binaryStr.length() - 32);
        System.out.println("Dest Addr " + destiAddr);
        // get next 6 bits
        String reqID_ = binaryStr.substring(binaryStr.length() - 54, binaryStr.length() - 48);
        System.out.println("Req ID " + reqID_);
        // get next 6 bits
        String hopCount_ = binaryStr.substring(binaryStr.length() - 60, binaryStr.length() - 54);
        System.out.println("Hop Count " + hopCount_);
        // get next 6 bits
        String flags_ = binaryStr.substring(binaryStr.length() - 66, binaryStr.length() - 60);
        System.out.println("Flags " + flags_);
        // get remaining bits
        String type_ = binaryStr.substring(0, binaryStr.length() - 66);
        System.out.println("Type " + Integer.parseInt(type_,2));


    }
}
